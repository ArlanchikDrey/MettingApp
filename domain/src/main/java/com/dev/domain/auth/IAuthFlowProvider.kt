
package com.dev.domain.auth

import com.dev.domain.user.UserState
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

/**
 *
 */

interface IAuthFlowProvider {
	
	fun getUserAuthState(): Observable<UserState>
	
	fun updateUserItem(user: UserItem): Completable
	
	fun logOut()
	
}