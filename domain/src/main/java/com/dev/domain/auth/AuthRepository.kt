
package com.dev.domain.auth

import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface AuthRepository<T> {

	fun signUp(userItem: UserItem): Single<UserItem>

	fun signIn(acct: T): Completable

}