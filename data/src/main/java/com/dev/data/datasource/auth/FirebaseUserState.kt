
package com.dev.data.datasource.auth

import com.google.firebase.auth.FirebaseUser

/**
 *
 */

internal sealed class FirebaseUserState {
	data class NotNullUser(val user: FirebaseUser): FirebaseUserState()
	object NullUser: FirebaseUserState()
	
	companion object {
		fun pack(user: FirebaseUser?) = if (user == null) NullUser else NotNullUser(user)
	}
}