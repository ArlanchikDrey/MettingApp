
package com.dev.meeting.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.dev.data.repository.auth.AuthFlowProvider
import com.dev.domain.conversations.ConversationItem
import com.dev.domain.pairs.MatchedUserItem
import com.dev.domain.user.UserState
import com.dev.domain.user.data.UserItem
import com.dev.meeting.core.log.logError
import com.dev.meeting.ui.common.base.BaseViewModel
import com.dev.meeting.ui.common.errors.ErrorType.AUTHENTICATING
import com.dev.meeting.ui.common.errors.MyError
import java.util.concurrent.TimeUnit.*
import java.util.concurrent.TimeoutException

/**
 * In general, you should strongly prefer passing only the minimal amount of data between destinations.
 * For example, you should pass a key to retrieve an object rather than passing the object itself,
 * as the total space for all saved states is limited on Android.
 * If you need to pass large amounts of data,
 * consider using a ViewModel as described in Share data between fragments.
 *
 * @see https://developer.android.com/guide/navigation/navigation-pass-data
 */

class SharedViewModel @ViewModelInject constructor(
	private val authFlow: AuthFlowProvider
): BaseViewModel() {

	val matchedUserItemSelected = MutableLiveData<MatchedUserItem>()
	val userNavigateTo = MutableLiveData<UserItem>()
	val conversationSelected = MutableLiveData<ConversationItem>()

	val userState = MutableLiveData<UserState>()
	val userInfoForRegistration = MutableLiveData<UserItem>()
	
	
	fun listenUserFlow() {
		disposables.add(
			authFlow.getUserAuthState()
				.subscribe(
					{ userState.postValue(it) },
					{
						if (it is TimeoutException)
							error.postValue(
								MyError(
									AUTHENTICATING,
									Exception(
										"Timeout occurred." +
										"\nThere might be a server error or your location could not be determined." +
										"\n Take into consideration that we can't retrieve your location from GPS if you are in the building." +
										"\nPlease, enable full location access in settings."
									)
								)
							)
						else error.postValue(MyError(AUTHENTICATING, it))
					}
				)
		
		)
	}
	
	fun logOut() = authFlow.logOut()
	
	fun updateUser(user: UserItem) {
		disposables.add(
			authFlow.updateUserItem(user)
				.subscribe(
					{
						MainActivity.currentUser = user
					},
					{
						logError(TAG, "$it")
					}
				)
		)
	}

}
