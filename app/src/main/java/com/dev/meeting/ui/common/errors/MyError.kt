
package com.dev.meeting.ui.common.errors

/**
 * Custom class that holds error, which probably comes from data request
 */

data class MyError(val errorType: ErrorType, val error: Throwable) {

	fun getErrorMessage() = error.localizedMessage ?: "Unknown error"

}