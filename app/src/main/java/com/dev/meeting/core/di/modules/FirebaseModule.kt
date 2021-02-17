
package com.dev.meeting.core.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.dev.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Core firebase components, I think @Singleton annotation here is useless due to .getInstance()
 * returns singletons by itself
 */

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

	@Provides
	@Singleton
	fun firebaseAuth() = FirebaseAuth.getInstance()

	@Provides
	@Singleton
	fun firebaseDatabase() = FirebaseFirestore.getInstance()

	@Provides
	@Singleton
	fun firebaseInstance() = FirebaseInstallations.getInstance()

	@Provides
	@Singleton
	fun firebaseStorage(): StorageReference {
		val storage = FirebaseStorage.getInstance()
		storage.maxDownloadRetryTimeMillis = 60000  // wait 1 min for downloads
		storage.maxOperationRetryTimeMillis = 10000  // wait 10s for normal ops
		storage.maxUploadRetryTimeMillis = 120000  // wait 2 mins for uploads
		return storage.getReferenceFromUrl(BuildConfig.FIREBASE_STORAGE_URL)
	}

}