
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