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