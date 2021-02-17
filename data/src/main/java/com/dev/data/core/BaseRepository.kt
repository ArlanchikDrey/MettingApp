
package com.dev.data.core

/**
 * This is the documentation block about the class
 */

abstract class BaseRepository {
	
	internal val TAG = "mylogs_${javaClass.simpleName}"

	protected companion object {
		// firestore users collection references
		const val USERS_COLLECTION = "users"
		const val USER_LIKED_COLLECTION = "liked"
		const val USER_MATCHED_COLLECTION = "matched"
		const val USER_SKIPPED_COLLECTION = "skipped"
		
		// firestore conversations reference
		const val CONVERSATIONS_COLLECTION = "conversations"
		const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		const val CONVERSATION_TIMESTAMP_FIELD = "lastMessageTimestamp"
		const val CONVERSATION_LAST_MESSAGE_TEXT_FIELD = "lastMessageText"
		const val CONVERSATION_DELETED_FIELD = "conversationDeleted"
		
		// Firebase Storage references
		const val GENERAL_FOLDER_STORAGE_IMG = "images"
		const val CONVERSATIONS_FOLDER_STORAGE_IMG = "conversations"

		const val MATCHED_DATE_FIELD = "matchedDate"

		const val USER_ID_FIELD = "userId"

	}

}