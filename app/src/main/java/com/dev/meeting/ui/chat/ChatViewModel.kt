
package com.dev.meeting.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.dev.domain.PaginationDirection
import com.dev.domain.PaginationDirection.*
import com.dev.domain.chat.ChatRepository
import com.dev.domain.chat.MessageItem
import com.dev.domain.conversations.ConversationItem
import com.dev.meeting.core.log.logDebug
import com.dev.meeting.core.log.logInfo
import com.dev.meeting.ui.MainActivity
import com.dev.meeting.ui.common.base.BaseViewModel
import com.dev.meeting.ui.common.errors.ErrorType
import com.dev.meeting.ui.common.errors.MyError


/**
 * [chatIsEmpty] used to mark conversation started or not to move partner out of pairs section
 */

class ChatViewModel @ViewModelInject constructor(
	private val repo: ChatRepository
): BaseViewModel() {
	
	val newMessage = MutableLiveData<MessageItem>()
	val initMessages = MutableLiveData<List<MessageItem>>()
	val nextMessages = MutableLiveData<List<MessageItem>>()
	
	val showLoading = MutableLiveData<Boolean>()
	val chatIsEmpty = MutableLiveData<Boolean>()

	//ui bind values
	val partnerName = MutableLiveData<String>("")
	val partnerPhoto = MutableLiveData<String>("")
	val isPartnerOnline = MutableLiveData<Boolean>(false)


	fun loadMessages(
		conversation: ConversationItem,
		lastMessage: MessageItem,
		direction: PaginationDirection
	) {
		disposables.add(repo.loadMessages(conversation, lastMessage, direction)
            .observeOn(mainThread())
            .subscribe(
				{
					when (direction) {
						INITIAL -> {
							initMessages.postValue(it)
							chatIsEmpty.postValue(it.isEmpty())
							logInfo(TAG, "initial loaded messages: ${it.size}")
						}
						NEXT -> {
							nextMessages.postValue(it)
							logInfo(TAG, "paginated loaded messages: ${it.size}")
						}
						else -> {}
					}
				},
				{ error.value = MyError(ErrorType.LOADING, it) }
            )
		)

	}

	fun observeNewMessages(conversation: ConversationItem) {
		disposables.add(repo.observeNewMessages(MainActivity.currentUser!!, conversation)
            .observeOn(mainThread())
            .subscribe(
				{ message ->
                    newMessage.postValue(message)
					chatIsEmpty.postValue(false)
					logDebug(TAG, "last received message: ${message.text}")
				},
				{ error.value = MyError(ErrorType.RECEIVING, it) }
            )
		)
	}


	fun sendMessage(text: String, conversation: ConversationItem): MessageItem {
		val message = MessageItem(
			sender = MainActivity.currentUser!!.baseUserInfo,
			recipientId = conversation.partner.userId,
			text = text,
			photoItem = null,
			conversationId = conversation.conversationId
		)
		disposables.add(repo.sendMessage(message, chatIsEmpty.value!!)
            .observeOn(mainThread())
            .subscribe(
				{ chatIsEmpty.postValue(false) },
				{ error.value = MyError(ErrorType.SENDING, it) }
			)
		)
		
		return message
	}

	//upload photo then send it as message item
	fun sendPhoto(photoUri: String, conversation: ConversationItem) {
		disposables.add(repo.uploadMessagePhoto(photoUri, conversation.conversationId)
            .flatMapCompletable {
	            val photoMessage = MessageItem(
		            sender = MainActivity.currentUser!!.baseUserInfo,
		            recipientId = conversation.partner.userId,
		            photoItem = it,
		            conversationId = conversation.conversationId
	            )
	            repo.sendMessage(photoMessage, chatIsEmpty.value!!)
            }
            .observeOn(mainThread())
            .subscribe(
				{ chatIsEmpty.postValue(false) },
				{ error.value = MyError(ErrorType.SENDING, it) }
			)
		)
	}
	
}