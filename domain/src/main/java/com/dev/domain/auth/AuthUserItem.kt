
package com.dev.domain.auth

import com.dev.domain.user.data.BaseUserInfo

/**
 * This is the documentation block about the class
 */

data class AuthUserItem(val baseUserInfo: BaseUserInfo = BaseUserInfo(),
                        val registrationTokens: List<String> = emptyList())