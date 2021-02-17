
package com.dev.domain.user.data

/**
 * Wrapper for two coordinates (latitude and longitude)
 */

data class LocationPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hash: String = ""
)