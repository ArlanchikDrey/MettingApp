
package com.dev.domain.chat

import com.dev.domain.PaginationDirection
import com.dev.domain.conversations.ConversationItem
import com.dev.domain.photo.PhotoItem
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ChatRepository {
	
	fun loadMessages(
		conversation: ConversationItem,
		lastMessage: MessageItem,
		direction: PaginationDirection
	): Single<List<MessageItem>>

	fun observeNewMessages(
        user: UserItem,
        conversation: ConversationItem
	): Observable<MessageItem>

	//fun observePartnerOnline(conversationId: String): Observable<Boolean>

	fun sendMessage(messageItem: MessageItem, emptyChat: Boolean): Completable
	
	fun uploadMessagePhoto(photoUri: String, conversationId: String): Single<PhotoItem>

}