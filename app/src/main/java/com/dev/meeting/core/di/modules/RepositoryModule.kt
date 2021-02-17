/*
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

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
