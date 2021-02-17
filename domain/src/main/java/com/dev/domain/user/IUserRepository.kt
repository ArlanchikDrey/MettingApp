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