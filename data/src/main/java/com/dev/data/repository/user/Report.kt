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

package com.dev.data.repository.user

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.dev.domain.user.data.BaseUserInfo
import com.dev.domain.user.data.ReportType

/**
 * reports data class
 */

internal data class Report(
    val reportType: ReportType,
    val reportedUser: BaseUserInfo,
    @ServerTimestamp val dateReported: Timestamp? = null
)