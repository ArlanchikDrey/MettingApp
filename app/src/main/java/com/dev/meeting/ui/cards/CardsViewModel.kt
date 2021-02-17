
package com.dev.meeting.ui.cards

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.dev.domain.cards.CardsRepository
import com.dev.domain.user.data.UserItem
import com.dev.meeting.core.log.logDebug
import com.dev.meeting.core.log.logInfo
import com.dev.meeting.ui.MainActivity
import com.dev.meeting.ui.cards.CardsViewModel.SwipeAction.*
import com.dev.meeting.ui.common.base.BaseViewModel
import com.dev.meeting.ui.common.errors.ErrorType
import com.dev.meeting.ui.common.errors.MyError

class CardsViewModel @ViewModelInject constructor(
	private val repo: CardsRepository
): BaseViewModel() {
	
	enum class SwipeAction {
		SKIP, LIKE
	}
	
	private var cardIndex = 0
	private val usersCardsList = MutableLiveData<List<UserItem>>()

	val showLoading = MutableLiveData<Boolean>()
	val showMatchDialog = MutableLiveData<UserItem>()
	val showEmptyIndicator = MutableLiveData<Boolean>()
	
	
	val topCard = MutableLiveData<UserItem?>(null)
	val bottomCard = MutableLiveData<UserItem?>(null)
	
	init {
		loadUsersByPreferences(true)
	}

	private fun addToSkipped(skippedUser: UserItem) {
		disposables.add(repo.skipUser(MainActivity.currentUser!!, skippedUser)
            .observeOn(mainThread()).subscribe(
				{ logDebug(TAG, "Skipped: $skippedUser") },
				{ error.value = MyError(ErrorType.SUBMITING, it) }
			)
		)
	}


	private fun checkMatch(likedUser: UserItem) {
		disposables.add(repo.likeUserAndCheckMatch(MainActivity.currentUser!!, likedUser)
            .observeOn(mainThread())
            .subscribe(
	            { if (it) showMatchDialog.value = likedUser },
	            { error.value = MyError(ErrorType.CHECKING, it) }
			)
		)
	}

	private fun loadUsersByPreferences(initialLoading: Boolean = false) {
		disposables.add(repo.getUsersByPreferences(MainActivity.currentUser!!, initialLoading)
            .observeOn(mainThread())
            .doOnSubscribe { showLoading.value = true }
			.doFinally { showLoading.value = false }
            .subscribe(
				{ cards ->
					showEmptyIndicator.postValue(cards.isNullOrEmpty())
					showLoading.postValue(cards.isNullOrEmpty())
					
					if (cards.isNotEmpty()) {
						cardIndex = 0
						
						usersCardsList.postValue(cards)
						
						topCard.postValue(cards.first())
						bottomCard.postValue(cards.drop(1).firstOrNull())
						
					}
					else {
						topCard.postValue(null)
						bottomCard.postValue(null)
					}
					
					logInfo(TAG, "loaded cards: ${cards.size}")
				},
				{ error.value = MyError(ErrorType.LOADING, it) }
			)
		)
	}
	
	fun swipeTop(swipeAction: SwipeAction) {
		cardIndex += 2
		when (swipeAction) {
			SKIP -> topCard.value?.let { addToSkipped(it) }
			LIKE -> topCard.value?.let { checkMatch(it) }.also {
				logInfo(TAG, "Liked top: ${topCard.value?.baseUserInfo?.name}")
			}
		}
		
		if (cardIndex >= usersCardsList.value!!.size) loadUsersByPreferences()
		else topCard.postValue(usersCardsList.value!!.getOrNull(cardIndex))
		
	}
	
	fun swipeBottom(swipeAction: SwipeAction) {
		when (swipeAction) {
			SKIP -> bottomCard.value?.let { addToSkipped(it) }
			LIKE -> bottomCard.value?.let { checkMatch(it) }.also {
				logInfo(TAG, "Liked bottom: ${bottomCard.value?.baseUserInfo?.name}")
			}
		}
		bottomCard.postValue(usersCardsList.value!!.getOrNull(cardIndex + 1))
	}
	
}

