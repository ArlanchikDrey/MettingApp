
package com.dev.meeting.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.dev.domain.auth.AuthRepository
import com.dev.domain.user.data.UserItem
import com.dev.meeting.core.log.logDebug
import com.dev.meeting.ui.common.base.BaseViewModel
import com.dev.meeting.ui.common.errors.ErrorType
import com.dev.meeting.ui.common.errors.ErrorType.AUTHENTICATING
import com.dev.meeting.ui.common.errors.MyError
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.util.concurrent.TimeoutException


class AuthViewModel @ViewModelInject constructor(
	private val repo: AuthRepository<GoogleSignInAccount>
) : BaseViewModel() {
	
	val signUpDone = MutableLiveData<UserItem>()
	val showLoading = MutableLiveData<Boolean>()
	
	fun signIn(acct: GoogleSignInAccount) {
		disposables.add(repo.signIn(acct)
			.doOnSubscribe { showLoading.value = true }
			.doFinally { showLoading.value = false }
            .observeOn(mainThread())
            .subscribe(
	            { logDebug(TAG, "Logged in successfully") },
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
            ))
	}

	fun signUp(userItem: UserItem) {
		disposables.add(repo.signUp(userItem)
            .observeOn(mainThread())
            .subscribe(
	            { signUpDone.postValue(it) },
	            { error.value = MyError(ErrorType.AUTHENTICATING, it) }
            )
		)
	}
	
}