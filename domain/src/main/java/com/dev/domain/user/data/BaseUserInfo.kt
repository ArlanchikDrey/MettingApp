
package com.dev.domain.user.data

import com.dev.domain.user.data.Gender.MALE

/**
 * This is the documentation block about the class
 */

data class BaseUserInfo(
    val name: String = "",
    val age: Int = 0,
    val gender: Gender = MALE,
    val mainPhotoUrl: String = "",
    val userId: String = ""
)