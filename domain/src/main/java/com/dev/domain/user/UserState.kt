
package com.dev.domain.user

import com.dev.domain.user.UserState.RegistrationStatus.REGISTERED
import com.dev.domain.user.UserState.RegistrationStatus.UNREGISTERED
import com.dev.domain.user.data.UserItem

/**
 * RxJava cannot emit a pure "null" so here is the wrapper
 */

sealed class UserState {
	data class AUTHENTICATED(val user: UserItem): UserState()
	object UNAUTHENTICATED: UserState()
	data class UNREGISTERED(val initialUserInfo: UserItem): UserState()
	
	inline fun <C> fold(
        authenticated: (UserItem) -> C,
        unauthenticated: () -> C,
        unregistered: (UserItem) -> C
	): C = when (this) {
		is AUTHENTICATED -> authenticated(user)
		is UNAUTHENTICATED -> unauthenticated()
		is UNREGISTERED -> unregistered(initialUserInfo)
	}
	
	companion object {
		val UNDEFINED = pack(null, UNREGISTERED)
		
		fun unregistered(user: UserItem) = pack(user, UNREGISTERED)
		fun registered(user: UserItem) = pack(user, REGISTERED)
		
		private fun pack(user: UserItem?, registration: RegistrationStatus) =
			if (user == null) UNAUTHENTICATED
			else when(registration) {
				REGISTERED -> AUTHENTICATED(user)
				UNREGISTERED -> UNREGISTERED(user)
			}
		
	}
	
	enum class RegistrationStatus {
		REGISTERED, UNREGISTERED
	}
}