
package com.dev.domain.conversations

import com.dev.domain.user.data.BaseUserInfo
import java.util.*

/**
 * This is the documentation block about the class
 */

data class ConversationItem(
    val partner: BaseUserInfo = BaseUserInfo(),
    val conversationId: String = "",
    val conversationStarted: Boolean = false,
    val lastMessageText: String = "",
    val lastMessageTimestamp: Date? = Date(),
    val partnerOnline: Boolean = false,
    val participants: Participants = Participants(),
    val unreadCount: Int = 0
)