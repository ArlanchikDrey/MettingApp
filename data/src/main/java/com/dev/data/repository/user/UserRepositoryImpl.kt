
package com.dev.data.repository.user

import com.google.firebase.firestore.FirebaseFirestore
import com.dev.data.core.BaseRepository
import com.dev.data.core.MySchedulers
import com.dev.data.core.firebase.asSingle
import com.dev.data.core.firebase.deleteAsCompletable
import com.dev.data.core.firebase.setAsCompletable
import com.dev.domain.pairs.MatchedUserItem
import com.dev.domain.user.IUserRepository
import com.dev.domain.user.data.BaseUserInfo
import com.dev.domain.user.data.ReportType
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * [IUserRepository]
 * Current user related access to db to manipulate own data
 */

class UserRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore
): BaseRepository(), IUserRepository {

	companion object {
		private const val REPORTS_COLLECTION = "reports"
	}
	
	override fun deleteMatchedUser(
        user: UserItem,
        matchedUserItem: MatchedUserItem
	): Completable = Completable.concatArray(
		deleteFromMatch(
			userForWhichDelete = user.baseUserInfo,
			userWhomToDelete = matchedUserItem.baseUserInfo,
			conversationId = matchedUserItem.conversationId
		),
		
		deleteFromMatch(
			userForWhichDelete = matchedUserItem.baseUserInfo,
			userWhomToDelete = user.baseUserInfo,
			conversationId = matchedUserItem.conversationId
		)
	).subscribeOn(MySchedulers.io())
	
	private fun deleteFromMatch(
        userForWhichDelete: BaseUserInfo,
        userWhomToDelete: BaseUserInfo,
        conversationId: String
	) = Completable.concatArray(
		// delete from matches
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(USER_MATCHED_COLLECTION)
			.document(userWhomToDelete.userId)
			.deleteAsCompletable(),
			
		// delete from conversations
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(conversationId)
			.deleteAsCompletable(),
			
		// add to skipped collection
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(USER_SKIPPED_COLLECTION)
			.document(userWhomToDelete.userId)
			.setAsCompletable(mapOf(USER_ID_FIELD to userWhomToDelete.userId))
	).subscribeOn(MySchedulers.io())
	

	override fun getRequestedUserItem(baseUserInfo: BaseUserInfo): Single<UserItem> =
		fs.collection(USERS_COLLECTION)
			.document(baseUserInfo.userId)
			.get()
			.asSingle()
			.map {
				if (it.exists()) it.toObject(UserItem::class.java)
				else UserItem(BaseUserInfo("DELETED"))
			}

	
	override fun submitReport(type: ReportType, baseUserInfo: BaseUserInfo): Completable =
		fs.collection(REPORTS_COLLECTION)
			.document()
			.setAsCompletable(Report(reportType = type, reportedUser = baseUserInfo))
	
}