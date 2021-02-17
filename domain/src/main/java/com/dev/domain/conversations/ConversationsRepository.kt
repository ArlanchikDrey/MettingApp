
package com.dev.domain.conversations

import com.dev.domain.PaginationDirection
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*

/**
 * Repository responsible for manipulating conversations
 */

interface ConversationsRepository {

	fun deleteConversation(user: UserItem, conversation: ConversationItem): Completable
	
	fun getConversations(
		user: UserItem,
		conversationTimestamp: Date,
		page: Int,
		direction: PaginationDirection
	): Single<List<ConversationItem>>

}