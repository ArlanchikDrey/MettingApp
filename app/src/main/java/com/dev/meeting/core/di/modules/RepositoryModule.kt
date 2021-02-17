
package com.dev.meeting.core.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.dev.data.datasource.auth.AuthCollector
import com.dev.data.repository.auth.AuthRepositoryImpl
import com.dev.data.repository.cards.CardsRepositoryImpl
import com.dev.data.repository.chat.ChatRepositoryImpl
import com.dev.data.repository.conversations.ConversationsRepositoryImpl
import com.dev.data.repository.pairs.PairsRepositoryImpl
import com.dev.data.repository.user.SettingsRepositoryImpl
import com.dev.data.repository.user.UserRepositoryImpl
import com.dev.domain.auth.AuthRepository
import com.dev.domain.cards.CardsRepository
import com.dev.domain.chat.ChatRepository
import com.dev.domain.conversations.ConversationsRepository
import com.dev.domain.pairs.PairsRepository
import com.dev.domain.user.ISettingsRepository
import com.dev.domain.user.IUserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
	
	@Provides
	@Singleton
	fun authCollector(auth: FirebaseAuth): AuthCollector = AuthCollector(auth)
	
	@Provides
	fun authRepository(repository: AuthRepositoryImpl<GoogleSignInAccount>):
			AuthRepository<GoogleSignInAccount> = repository

	@Provides
	fun cardsRepository(repository: CardsRepositoryImpl): CardsRepository = repository

	@Provides
	fun chatRepository(repository: ChatRepositoryImpl): ChatRepository = repository

	@Provides
	fun conversationsRepository(repository: ConversationsRepositoryImpl): ConversationsRepository = repository

	@Provides
	fun pairsRepository(repository: PairsRepositoryImpl): PairsRepository = repository

	@Provides
	fun userRepository(repository: UserRepositoryImpl): IUserRepository = repository
	
	@Provides
	fun userSettingsRepository(repository: SettingsRepositoryImpl): ISettingsRepository = repository

}
