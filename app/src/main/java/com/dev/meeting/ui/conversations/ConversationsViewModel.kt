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

package com.dev.meeting.ui.conversations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.dev.domain.PaginationDirection.*
import com.dev.domain.conversations.ConversationItem
import com.dev.domain.conversations.ConversationsRepository
import com.dev.meeting.core.log.logError
import com.dev.meeting.ui.MainActivity
import com.dev.meeting.ui.common.base.BaseViewModel
import com.dev.meeting.ui.common.errors.ErrorType
import com.dev.meeting.ui.common.errors.MyError
import java.util.*

class ConversationsViewModel @ViewModelInject constructor(
	private val repo: ConversationsRepository
): BaseViewModel() {
	
	private val deleteConversationStatus: MutableLiveData<Boolean> = MutableLiveData()

	val initConversations = MutableLiveData<List<ConversationItem>>()
	val nextConversations = MutableLiveData<List<ConversationItem>>()
	val prevConversations = MutableLiveData<List<ConversationItem>>()

	val showTextHelper = MutableLiveData<Boolean>()

	init {
		loadInitConversations()
	}

	fun deleteConversation(conversationItem: ConversationItem){
		disposables.add(repo.deleteConversation(MainActivity.currentUser!!, conversationItem)
            .observeOn(mainThread())
            .subscribe(
	            { deleteConversationStatus.value = true },
	            {
		            logError(TAG, "$it")
		            deleteConversationStatus.value = false
		            error.value = MyError(ErrorType.DELETING, it)
	            }
            )
		)
	}


	private fun loadInitConversations() {
		disposables.add(repo.getConversations(MainActivity.currentUser!!, Date(), 0, INITIAL)
            .observeOn(mainThread())
            .subscribe(
	            { conversations ->
		            initConversations.postValue(conversations)
		            showTextHelper.postValue(conversations.isEmpty())
	            },
	            { error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}
	
	fun loadPrevConversations(page: Int) {
		disposables.add(repo.getConversations(MainActivity.currentUser!!, Date(), page, PREVIOUS)
			.observeOn(mainThread())
			.subscribe(
				{ prevConversations.postValue(it) },
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}
	
	fun loadNextConversations(conversationTimestamp: Date, page: Int) {
		disposables.add(repo.getConversations(MainActivity.currentUser!!, conversationTimestamp, page, NEXT)
			.observeOn(mainThread())
			.subscribe(
				{ nextConversations.postValue(it) },
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}

	fun getDeleteConversationStatus() = deleteConversationStatus
	
}