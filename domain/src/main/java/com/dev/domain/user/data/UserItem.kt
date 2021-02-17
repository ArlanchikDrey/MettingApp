
package com.dev.domain.user.data

import com.dev.domain.photo.PhotoItem

data class UserItem(
    val baseUserInfo: BaseUserInfo = BaseUserInfo(),
    val aboutText: String = "",
    val photoURLs: List<PhotoItem> = listOf(),
    val preferences: SelectionPreferences = SelectionPreferences(),
    val location: LocationPoint = LocationPoint()
)

