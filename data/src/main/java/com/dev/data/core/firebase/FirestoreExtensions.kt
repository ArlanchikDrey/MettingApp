
package com.dev.data.core.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.dev.data.core.MySchedulers
import com.dev.data.core.log.logDebug
import com.dev.data.core.log.logError
import com.dev.data.core.log.logWarn
import com.dev.domain.photo.PhotoItem
import com.dev.domain.user.data.BaseUserInfo
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate
import io.reactivex.rxjava3.internal.operators.single.SingleCreate

/**
 * Rx wrappers for firestore callbacks api
 */

private const val TAG = "mylogs_FirestoreExtensions"
internal const val FIRESTORE_NO_DOCUMENT_EXCEPTION = "No such document."


/**
 * CAUTION
 * DON'T USE THIS METHOD IF RESULT WILL BE <<VOID>>
 *
 * If you know that TResult expected VOID -> use completable instead
 * otherwise exception that rx operators are not allow emit null values will be thrown
 */
internal fun <TResult> Task<TResult>.asSingle(): Single<TResult> = SingleCreate<TResult> { emitter ->
	addOnSuccessListener {
		logDebug(TAG, "TResult is success")
		emitter.onSuccess(it)
	}
	addOnFailureListener {
		logDebug(TAG, "TResult is failure")
		emitter.onError(it)
	}
	emitter.setCancellable {  }
}.subscribeOn(MySchedulers.io())

internal fun <T> DocumentReference.getAndDeserializeAsSingle(clazz: Class<T>): Single<T> = SingleCreate<T> { emitter ->
	get()
		.addOnSuccessListener { snapshot ->
			logDebug(TAG, "Document retrieve success")
			if (snapshot.exists() && snapshot.data != null) {
				
				logDebug(TAG, "Data is not null, deserialization in process...")
				val serializedDoc: T = snapshot.toObject(clazz)!!
				
				logDebug(TAG, "Deserialization to ${clazz.simpleName} succeed...")
				emitter.onSuccess(serializedDoc)
			}
			else {
				logError(TAG, FIRESTORE_NO_DOCUMENT_EXCEPTION)
				emitter.onError(NoSuchElementException(FIRESTORE_NO_DOCUMENT_EXCEPTION))
			}
			
		}
		.addOnFailureListener { exception ->
			logError(TAG, "Failed to retrieve document from backend: $exception")
			emitter.onError(exception)
		}
	emitter.setCancellable {  }
}.subscribeOn(MySchedulers.io())


internal fun <T> Query.executeAndDeserializeSingle(clazz: Class<T>): Single<List<T>> = SingleCreate<List<T>> { emitter ->
	logDebug(TAG, "Trying to execute given query...")
	get()
		.addOnSuccessListener { querySnapshot ->
			if (!querySnapshot.isEmpty) {
				logDebug(TAG, "Query executed successfully, printing first and last doc...")
				querySnapshot.documents.run {
					logDebug(TAG, first().reference.path)
					logDebug(TAG, last().reference.path)
					logDebug(TAG, "Query size = $size")
				}
				
				logDebug(TAG, "Query deserialization to ${clazz.simpleName} in process...")
				val resultList = querySnapshot.toObjects(clazz)
				
				logDebug(TAG, "Deserialization to ${clazz.simpleName} succeed...")
				emitter.onSuccess(resultList)
			}
			else {
				logWarn(TAG, "Query result is empty.")
				emitter.onSuccess(emptyList())
			}
			
		}
		.addOnFailureListener { exception ->
			logError(TAG, "Failed to execute given query: $exception")
			emitter.onError(exception)
		}
	
	emitter.setCancellable {  }
}.subscribeOn(MySchedulers.io())




internal fun <T> DocumentReference.setAsCompletable(dataClass: T): Completable = CompletableCreate { emitter ->
	this.set(dataClass!!)
		.addOnSuccessListener {
			logDebug(TAG, "Set $dataClass as document successfully")
			emitter.onComplete()
		}
		.addOnFailureListener { exception ->
			logError(TAG, "set $dataClass as document error: $exception")
			emitter.onError(exception)
		}
	emitter.setCancellable {  }
}.subscribeOn(MySchedulers.io())


internal fun DocumentReference.updateAsCompletable(field: String, value: Any?): Completable = CompletableCreate { emitter ->
	update(mapOf(field to value))
		.addOnSuccessListener {
			logDebug(TAG, "Field $field updated with $value successfully")
			emitter.onComplete()
		}
		.addOnFailureListener { exception ->
			logError(TAG, "Field $field updated with $value error: $exception")
			emitter.onError(exception)
		}
	emitter.setCancellable {  }
}.subscribeOn(MySchedulers.io())

internal fun DocumentReference.deleteAsCompletable(): Completable = CompletableCreate { emitter ->
	delete()
		.addOnSuccessListener {
			logDebug(TAG, "Document with $path deleted successfully")
			emitter.onComplete()
		}.addOnFailureListener { exception ->
			logError(TAG, "Document with $path deleting error: $exception")
			emitter.onError(exception)
		}
	emitter.setCancellable { }
	
}.subscribeOn(MySchedulers.io())
	


internal fun FirebaseUser.toUserItem(): UserItem = UserItem(
    baseUserInfo = BaseUserInfo(
		name = displayName!!,
		mainPhotoUrl = "${photoUrl}?height=1000",
		userId = uid
	), photoURLs = listOf(PhotoItem.FACEBOOK_PHOTO("${photoUrl}?height=1000"))
)