
package com.dev.data.repository.chat

import android.net.Uri
import android.text.format.DateFormat
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.storage.StorageReference
import com.dev.data.core.BaseRepository
import com.dev.data.core.MySchedulers
import com.dev.data.core.firebase.asSingle
import com.dev.data.core.firebase.executeAndDeserializeSingle
import com.dev.data.core.firebase.setAsCompletable
import com.dev.data.core.firebase.updateAsCompletable
import com.dev.data.core.log.logDebug
import com.dev.domain.PaginationDirection
import com.dev.domain.PaginationDirection.NEXT
import com.dev.domain.chat.ChatRepository
import com.dev.domain.chat.MessageItem
import com.dev.domain.conversations.ConversationItem
import com.dev.domain.photo.PhotoItem
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate
import java.util.*
import javax.inject.Inject

/**
 * [ChatRepository] implementation
 * [observeNewMessages] method uses firebase snapshot listener
 * read more about local changes and writing to backend with "latency compensation"
 * @link https://firebase.google.com/docs/firestore/query-data/listen
 */

class ChatRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore,
	private val storage: StorageReference
): BaseRepository(), ChatRepository {
	
	companion object {
		private const val CONVERSATION_PARTNER_ONLINE_FIELD = "partnerOnline"
		private const val CONVERSATION_UNREAD_COUNT_FIELD = "unreadCount"
		private const val MESSAGES_COLLECTION = "messages"
		private const val MESSAGE_TIMESTAMP_FIELD = "timestamp"
		
		private const val LAST_MESSAGE_PHOTO = "Photo"
		
		private const val SOURCE_SERVER = "Server"
		private const val SOURCE_LOCAL = "Local"
	}
	
	private fun messagesQuery(conversation: ConversationItem): Query =
		fs.collection(CONVERSATIONS_COLLECTION)
			.document(conversation.conversationId)
			.collection(MESSAGES_COLLECTION)
			.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.limit(20)
	
	private var isPartnerOnline = false
	
	/**
	 * this flag won't allow observable to emit new messages on initial loading
	 * for initial loading see [loadMessages] method
	 */
	private var isSnapshotInitiated = false
	
	override fun loadMessages(
		conversation: ConversationItem,
		lastMessage: MessageItem,
		direction: PaginationDirection
	): Single<List<MessageItem>> = (
		if (direction == NEXT) messagesQuery(conversation).startAfter(lastMessage.timestamp)
		else messagesQuery(conversation)
	).executeAndDeserializeSingle(MessageItem::class.java)
	
	
	override fun observeNewMessages(
        user: UserItem,
        conversation: ConversationItem
	): Observable<MessageItem> = ObservableCreate<MessageItem> { emitter ->
		val listener = fs.collection(CONVERSATIONS_COLLECTION)
			.document(conversation.conversationId)
			.collection(MESSAGES_COLLECTION)
			.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.limit(1)
			.addSnapshotListener { snapshot, e ->
				if (e != null) {
					emitter.onError(e)
					return@addSnapshotListener
				}
				
				val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) SOURCE_LOCAL
				else SOURCE_SERVER
				
				if (!snapshot?.documents.isNullOrEmpty() && source == SOURCE_SERVER) {
					
					//documentChanges has various states, parse them
					snapshot!!.documentChanges.forEach { documentChange ->
						if (documentChange.type == DocumentChange.Type.ADDED) {
							
							val document = snapshot.documents.first()
							if (document.getField<String>("recipientId") == user.baseUserInfo.userId) {
								logDebug(TAG, "Message was sent not from this user")
								
								if (isSnapshotInitiated)
									emitter.onNext(document.toObject(MessageItem::class.java))
							}
							
						}
					}
				}
				else {
					logDebug(TAG, "Message comes from $source source")
				}
				
				isSnapshotInitiated = true
				
			}
		emitter.setCancellable { listener.remove() }
	}.subscribeOn(MySchedulers.io())


	override fun sendMessage(messageItem: MessageItem, emptyChat: Boolean): Completable {
		
		val conversation = fs
			.collection(CONVERSATIONS_COLLECTION)
			.document(messageItem.conversationId)

		messageItem.timestamp = FieldValue.serverTimestamp()

		return Completable.concatArray(
			//send message to server
			conversation.collection (MESSAGES_COLLECTION)
				.document()
				.setAsCompletable(messageItem),
			
			//update started status or skip
			if (emptyChat) updateStartedStatus(messageItem)
			else Completable.complete(),
			
			//update last message for both users
			updateLastMessage(messageItem)
		)
	}
	
	private fun updateStartedStatus(message: MessageItem) = Completable.concatArray(
		// for current
		fs.collection(USERS_COLLECTION)
			.document(message.sender.userId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(message.conversationId)
			.updateAsCompletable(CONVERSATION_STARTED_FIELD, true),
		
		fs.collection(USERS_COLLECTION)
			.document(message.sender.userId)
			.collection(USER_MATCHED_COLLECTION)
			.document(message.recipientId)
			.updateAsCompletable(CONVERSATION_STARTED_FIELD, true),
		
		// for partner
		fs.collection(USERS_COLLECTION)
			.document(message.recipientId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(message.conversationId)
			.updateAsCompletable(CONVERSATION_STARTED_FIELD, true),
		
		fs.collection(USERS_COLLECTION)
			.document(message.recipientId)
			.collection(USER_MATCHED_COLLECTION)
			.document(message.sender.userId)
			.updateAsCompletable(CONVERSATION_STARTED_FIELD, true)
	
	).subscribeOn(MySchedulers.io())
	
	
	override fun uploadMessagePhoto(photoUri: String, conversationId: String): Single<PhotoItem> {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()+".jpg"
		val storageRef = storage
			.child(GENERAL_FOLDER_STORAGE_IMG)
			.child(CONVERSATIONS_FOLDER_STORAGE_IMG)
			.child(conversationId)
			.child(namePhoto)
			.putFile(Uri.parse(photoUri))
			
		return storageRef.asSingle()
			.flatMap { task ->
				task.storage
					.downloadUrl
					.asSingle()
					.map { PhotoItem(fileName = namePhoto, fileUrl = it.toString()) }
			}
	}
	
	
	/**
	 * update last messages in collections
	 * this last message directly relates to conversations
	 * when user enters a conversations fragment and see conversations list the last message is
	 * visible as last one received
	 * Updates can be called from both sides of chat participants, so here we can see a problem:
	 * when one user have bad internet connection and other sends message a collision expected
	 */
	private fun updateLastMessage(message: MessageItem): Completable {
		val cur = fs.collection(USERS_COLLECTION)
			.document(message.sender.userId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(message.conversationId)

		val par = fs.collection(USERS_COLLECTION)
			.document(message.recipientId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(message.conversationId)
		
		return if (message.photoItem != null) {
			Completable.concatArray(
				// for current
				cur.updateAsCompletable(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, LAST_MESSAGE_PHOTO),
				cur.updateAsCompletable(CONVERSATION_TIMESTAMP_FIELD, message.timestamp),
				// for partner
				par.updateAsCompletable(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, LAST_MESSAGE_PHOTO),
				par.updateAsCompletable(CONVERSATION_TIMESTAMP_FIELD, message.timestamp)
			)
		}
		else {
			Completable.concatArray(
				// for current
				cur.updateAsCompletable(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, message.text),
				cur.updateAsCompletable(CONVERSATION_TIMESTAMP_FIELD, message.timestamp),
				// for partner
				par.updateAsCompletable(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, message.text),
				par.updateAsCompletable(CONVERSATION_TIMESTAMP_FIELD, message.timestamp)
			)
		}
	}

	//update unread count if partner is not online in chat
	private fun updateUnreadMessagesCount(message: MessageItem) =
		fs.collection(USERS_COLLECTION)
			.document(message.recipientId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(message.conversationId)
			.updateAsCompletable(CONVERSATION_UNREAD_COUNT_FIELD, FieldValue.increment(1))
}