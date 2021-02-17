
package com.dev.domain.pairs

import com.dev.domain.user.data.BaseUserInfo
import java.util.*

data class MatchedUserItem(
    val baseUserInfo: BaseUserInfo = BaseUserInfo(),
    val conversationStarted: Boolean = false,
    var conversationId: String = "",
    val matchedDate: Date = Date()
)