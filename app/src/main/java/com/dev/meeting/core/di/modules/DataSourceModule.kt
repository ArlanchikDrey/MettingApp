

package com.dev.meeting.core.di.modules

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.dev.data.datasource.UserDataSource
import com.dev.data.datasource.location.LocationDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *
 */

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
	
	@Provides
	@Singleton
	fun userDataSource(fs: FirebaseFirestore) = UserDataSource(fs)
	
	@Provides
	@Singleton
	fun locationDataSource(@ApplicationContext appContext: Context) = LocationDataSource(appContext)
	
	@Provides
	@Singleton
	fun appContext(@ApplicationContext appContext: Context): Context = appContext
	
}