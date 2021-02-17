
package com.dev.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.dev.data.core.firebase.getAndDeserializeAsSingle
import com.dev.data.core.firebase.setAsCompletable
import com.dev.data.core.firebase.updateAsCompletable
import com.dev.domain.user.data.UserItem

/**
 *
 */

class UserDataSource(private val fs: FirebaseFirestore) {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
	}
	
	fun getFirestoreUser(id: String) = fs.collection(FS_USERS_COLLECTION)
		.document(id)
		.getAndDeserializeAsSingle(UserItem::class.java)
	
	fun updateFirestoreUserField(
		id: String, field: String, value: Any
	) = fs.collection(FS_USERS_COLLECTION)
		.document(id)
		.updateAsCompletable(field, value)
	
	fun writeFirestoreUser(user: UserItem) = fs.collection(FS_USERS_COLLECTION)
		.document(user.baseUserInfo.userId)
		.setAsCompletable(user)
}