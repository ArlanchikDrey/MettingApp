
package com.dev.domain.user

import com.dev.domain.pairs.MatchedUserItem
import com.dev.domain.user.data.BaseUserInfo
import com.dev.domain.user.data.ReportType
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface IUserRepository {

	fun deleteMatchedUser(user: UserItem, matchedUserItem: MatchedUserItem): Completable

	fun getRequestedUserItem(baseUserInfo: BaseUserInfo): Single<UserItem>

	fun submitReport(type: ReportType, baseUserInfo: BaseUserInfo): Completable

}