
package com.dev.domain.user.data

import com.dev.domain.user.data.SelectionPreferences.PreferredGender.EVERYONE

/**
 *
 * @param radius represents preferred distance in kilometers to retrieve users to show
 */

data class SelectionPreferences(
    val gender: PreferredGender = EVERYONE,
    val ageRange: PreferredAgeRange = PreferredAgeRange(),
    val radius: Double = 100.0
) {
    
    data class PreferredAgeRange(val minAge: Int = 18, val maxAge: Int = 18)
    
    enum class PreferredGender {
        MALE, FEMALE, EVERYONE
    }
}
