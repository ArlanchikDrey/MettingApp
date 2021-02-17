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