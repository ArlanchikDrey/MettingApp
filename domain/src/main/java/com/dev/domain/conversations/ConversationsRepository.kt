/*
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

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