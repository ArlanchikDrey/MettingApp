
package com.dev.meeting.core.glide

/**
 * this interface is responsible for updating the UI,
 * which updates the progress of the ProgressBar
 *
 * @param granularityPercentage controls how often the listener needs an update.
 * 0% and 100% will always be dispatched.
 * For example, if you return one, it will dispatch at most 100 times,
 * with each time representing at least one percent of the progress.
 */

interface UIonProgressListener {

	val granularityPercentage: Float

	fun onProgress(bytesRead: Long, expectedLength: Long)
}