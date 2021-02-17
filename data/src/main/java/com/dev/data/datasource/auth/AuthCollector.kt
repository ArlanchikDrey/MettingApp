
package com.dev.data.datasource.auth

import com.google.firebase.auth.FirebaseAuth
import com.dev.data.core.log.logDebug
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate

/**
 * Class used only to provide [FirebaseAuth] callbacks as [Observable]
 */

class AuthCollector(auth: FirebaseAuth) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	internal val firebaseAuthObservable: Observable<FirebaseAuth> = ObservableCreate { emitter ->
		
		val listener = FirebaseAuth.AuthStateListener { emitter.onNext(it) }
		
		auth.addAuthStateListener(listener)
		logDebug(TAG, "AuthListener attached.")
		
		emitter.setCancellable {
			logDebug(TAG, "AuthListener removed.")
			auth.removeAuthStateListener(listener)
		}
	}
	
}