package com.dev.data.repository.conversations

import android.util.SparseArray
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.dev.data.core.BaseRepository
import com.dev.data.core.MySchedulers
import com.dev.data.core.firebase.asSingle
import com.dev.data.core.firebase.deleteAsCompletable
import com.dev.data.core.firebase.executeAndDeserializeSingle
import com.dev.data.core.firebase.setAsCompletable
import com.dev.domain.PaginationDirection
import com.dev.domain.PaginationDirection.*
import com.dev.domain.conversations.ConversationItem
import com.dev.domain.conversations.ConversationsRepository
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

/**
 * [ConversationsRepository] implementation
 */

class ConversationsRepositoryImpl @Inject constructor(
    private val fs: FirebaseFirestore
) : BaseRepository(), ConversationsRepository {


    private val pages = SparseArray<Query>()


    private fun conversationsQuery(user: UserItem): Query = fs.collection(USERS_COLLECTION)
        .document(user.baseUserInfo.userId)
        .collection(CONVERSATIONS_COLLECTION)
        .orderBy(CONVERSATION_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
        .whereEqualTo(CONVERSATION_STARTED_FIELD, true)


    override fun deleteConversation(user: UserItem, conversation: ConversationItem): Completable =
        Completable.concatArray(
            deleteFromPartner(user, conversation),
            deleteFromMySelf(user, conversation)
        ).subscribeOn(MySchedulers.io())

    private fun deleteFromMySelf(user: UserItem, conversation: ConversationItem): Completable =
        fs.collection(CONVERSATIONS_COLLECTION)
            .document(conversation.conversationId)
            //mark that conversation no need to be exists
            //this need for cloud functions to delete whole chat room (not implemented)
            .setAsCompletable(mapOf(CONVERSATION_DELETED_FIELD to true))
            .andThen(
                Completable.concatArray(
                    fs.collection(USERS_COLLECTION)
                        .document(user.baseUserInfo.userId)
                        .collection(CONVERSATIONS_COLLECTION)
                        .document(conversation.conversationId)
                        .deleteAsCompletable(),

                    //current user delete from matched list
                    fs.collection(USERS_COLLECTION)
                        .document(user.baseUserInfo.userId)
                        .collection(USER_MATCHED_COLLECTION)
                        .document(conversation.partner.userId)
                        .deleteAsCompletable(),

                    //add to skipped collection
                    fs.collection(USERS_COLLECTION)
                        .document(user.baseUserInfo.userId)
                        .collection(USER_SKIPPED_COLLECTION)
                        .document(conversation.partner.userId)
                        .setAsCompletable(mapOf(USER_ID_FIELD to conversation.partner.userId))
                ).subscribeOn(MySchedulers.io())
            ).subscribeOn(MySchedulers.io())


    private fun deleteFromPartner(user: UserItem, conversation: ConversationItem): Completable =
        fs.collection(USERS_COLLECTION)
            .document(conversation.partner.userId)
            .get()
            .asSingle()
            .concatMapCompletable {
                if (it.exists()) {
                    Completable.concatArray(
                        //partner delete from matched list
                        it.reference
                            .collection(USER_MATCHED_COLLECTION)
                            .document(user.baseUserInfo.userId)
                            .deleteAsCompletable(),

                        //partner delete from conversations list
                        it.reference
                            .collection(CONVERSATIONS_COLLECTION)
                            .document(conversation.conversationId)
                            .deleteAsCompletable(),

                        //add to skipped collection
                        it.reference
                            .collection(USER_SKIPPED_COLLECTION)
                            .document(user.baseUserInfo.userId)
                            .setAsCompletable(mapOf(USER_ID_FIELD to user.baseUserInfo.userId))
                    ).subscribeOn(MySchedulers.io())

                } else Completable.complete()
            }

    override fun getConversations(
        user: UserItem,
        conversationTimestamp: Date,
        page: Int,
        direction: PaginationDirection
    ): Single<List<ConversationItem>> = when (direction) {

        INITIAL -> conversationsQuery(user).limit(20).also { pages.put(0, it) }

        NEXT -> {
            if (pages.contains(page)) {
                pages[page]
            } else {
                conversationsQuery(user)
                    .startAfter(conversationTimestamp)
                    .limit(20)
                    .also {
                        pages.put(page, it)
                    }
            }

        }

        PREVIOUS -> pages[page]

    }!!.executeAndDeserializeSingle(ConversationItem::class.java)

}