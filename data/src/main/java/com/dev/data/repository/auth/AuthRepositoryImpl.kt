
package com.dev.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.installations.FirebaseInstallations
import com.dev.data.core.BaseRepository
import com.dev.data.core.firebase.asSingle
import com.dev.data.datasource.UserDataSource
import com.dev.data.datasource.location.LocationDataSource
import com.dev.domain.auth.AuthRepository
import com.dev.domain.user.data.BaseUserInfo
import com.dev.domain.user.data.UserItem
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

/**
 * login chain: signInWithFacebook => check if exist registered user with same uId =>
 * handle registration => after all complete fetch last results
 * fetchUserInfo() always called last no matter is user just registered or not
 *
 */

class AuthRepositoryImpl<T> @Inject constructor(
	private val auth: FirebaseAuth,
	private val userDataSource: UserDataSource,
	private val locationDataSource: LocationDataSource
): BaseRepository(), AuthRepository<T> {
	
	companion object {
		private const val FS_INSTALLATIONS_FIELD = "installations"
	}

	override fun signIn(acct: T): Completable = signInWithGoogle(acct as GoogleSignInAccount)

	/**
	 * create new [UserItem] documents in db
	 */
	override fun signUp(userItem: UserItem): Single<UserItem> = Single.just(userItem)
		.zipWith(
			locationDataSource.locationSingle(),
			BiFunction { user, location  ->
				return@BiFunction user.copy(location = location)
			}
		)
		.concatMapCompletable { userDataSource.writeFirestoreUser(it) }
		.delay(500, MILLISECONDS)
		.andThen(updateInstallations(userItem.baseUserInfo.userId))
		.andThen(userDataSource.getFirestoreUser(userItem.baseUserInfo.userId))
	
	/**
	 * this fun is called first when user is trying to sign in via facebook
	 * creates a basic [BaseUserInfo] object based on public facebook profile
	 */
	private fun signInWithGoogle(acct: GoogleSignInAccount) =
		auth.signInWithCredential(GoogleAuthProvider.getCredential(acct.idToken, null))
			.asSingle()
			.concatMapCompletable {
				if (it.user != null) {
					val firebaseUser = it.user!!
					updateInstallations(firebaseUser.uid)
				}
				else Completable.error(IllegalStateException("User is not authenticated"))
			}
	
	private fun updateInstallations(id: String) = FirebaseInstallations
		.getInstance()
		.id
		.asSingle()
		.concatMapCompletable { token ->
			userDataSource.updateFirestoreUserField(
				id,
				FS_INSTALLATIONS_FIELD,
				FieldValue.arrayUnion(token)
			)
		}

}


