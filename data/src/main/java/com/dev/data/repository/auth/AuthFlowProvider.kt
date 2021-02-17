
package com.dev.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.dev.data.core.firebase.FIRESTORE_NO_DOCUMENT_EXCEPTION
import com.dev.data.core.firebase.toUserItem
import com.dev.data.core.log.logDebug
import com.dev.data.core.log.logError
import com.dev.data.datasource.UserDataSource
import com.dev.data.datasource.auth.AuthCollector
import com.dev.data.datasource.auth.FirebaseUserState
import com.dev.data.datasource.auth.FirebaseUserState.NotNullUser
import com.dev.data.datasource.location.LocationDataSource
import com.dev.domain.auth.IAuthFlowProvider
import com.dev.domain.user.UserState
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import java.util.concurrent.TimeUnit.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 */

@Singleton
class AuthFlowProvider @Inject constructor(
	private val auth: FirebaseAuth,
	private val location: LocationDataSource,
	private val userDataSource: UserDataSource
): IAuthFlowProvider {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	companion object {
		private const val LOCATION_FIELD = "location"
	}
	
	private val authObservable: Observable<FirebaseUserState> = AuthCollector(auth).firebaseAuthObservable.map {
		it.currentUser?.reload()
		FirebaseUserState.pack(it.currentUser)
	}
	
	override fun getUserAuthState(): Observable<UserState> = authObservable.switchMap { firebaseUser ->
		logDebug(TAG, "Collecting auth information...")
		
		if (firebaseUser is NotNullUser) {
			
			logDebug(TAG, "Auth info exists: ${firebaseUser.user.uid}")
			
			getUserFromRemoteStorage(firebaseUser.user)
		}
		//not signed in
		else {
			logError(TAG, "Auth info does not exists...")
			
			Observable.just(UserState.UNDEFINED)
		}
	}
	
	
	private fun getUserFromRemoteStorage(firebaseUser: FirebaseUser) =
		userDataSource.getFirestoreUser(firebaseUser.uid)
			.zipWith(
				//timeout if no location emitted
				location.locationSingle(),
				BiFunction { user, location ->
					return@BiFunction user.copy(location = location)
				}
			)
			.toObservable()
			.map {
				userDataSource.updateFirestoreUserField(
					it.baseUserInfo.userId,
					LOCATION_FIELD,
					it.location
				).subscribe {
					logDebug(TAG, "Location was updated")
				}
				UserState.registered(it)
			}
			.onErrorResumeNext { throwable ->
				logError(TAG, "$throwable")
				when (throwable) {
					is NoSuchElementException -> {
						//if no document stored on backend
						if (throwable.message == FIRESTORE_NO_DOCUMENT_EXCEPTION)
							Observable.just(UserState.unregistered(firebaseUser.toUserItem()))
						else Observable.just(UserState.UNDEFINED)
					}
					
					else -> Observable.error(throwable)
				}
				
			}
	
	
	override fun updateUserItem(user: UserItem): Completable =
		userDataSource.writeFirestoreUser(user)
	
	
	override fun logOut(){
		if (auth.currentUser != null) {
			auth.signOut()
		}
	}
	
}