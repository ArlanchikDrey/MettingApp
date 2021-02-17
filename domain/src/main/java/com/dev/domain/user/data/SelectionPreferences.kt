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
