
package com.dev.domain.chat

import com.dev.domain.photo.PhotoItem
import com.dev.domain.user.data.BaseUserInfo

data class MessageItem(
    val sender: BaseUserInfo = BaseUserInfo(),
    val recipientId: String = "",
    val text: String = "",
    var timestamp: Any? = null,
    val photoItem: PhotoItem? = PhotoItem(),
    val conversationId: String = ""
)