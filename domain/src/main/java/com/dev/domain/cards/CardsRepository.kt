
package com.dev.domain.cards

import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


interface CardsRepository {

	fun skipUser(user: UserItem, skippedUser: UserItem): Completable

	fun likeUserAndCheckMatch(user: UserItem, likedUserItem: UserItem): Single<Boolean>

	fun getUsersByPreferences(user: UserItem, initialLoading: Boolean): Single<List<UserItem>>

}
