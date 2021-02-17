
package com.dev.data.core.firebase

import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.dev.data.core.MySchedulers
import com.dev.data.core.log.logDebug
import com.dev.data.core.log.logError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate

/**
 *
 */

private const val TAG = "mylogs_FirebaseExtensions"

internal fun StorageReference.deleteAsCompletable(): Completable = CompletableCreate { emitter ->
	delete()
		.addOnSuccessListener {
			logDebug(TAG, "Delete file at $path successfully")
			emitter.onComplete()
		}
		.addOnFailureListener { exception ->
			if (exception is StorageException && exception.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND)
				emitter.onComplete()
			else {
				logError(TAG, "Delete file at $path with error: $exception")
				emitter.onError(exception)
			}
		}
	
}.subscribeOn(MySchedulers.io())