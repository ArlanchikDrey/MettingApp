
package com.dev.meeting.core.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.dev.data.datasource.auth.AuthCollector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule{

	@Provides
	@Singleton
	fun authCollector(auth: FirebaseAuth): AuthCollector = AuthCollector(auth)
	
}