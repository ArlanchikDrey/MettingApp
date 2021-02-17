
package com.dev.meeting.core.glide

import okhttp3.HttpUrl

/**
 * This interface is responsible for notifying
 * whoever is listening to the downloading progress of the URL.
 */

interface ResponseProgressListener {
	fun update(url: HttpUrl, bytesRead: Long, contentLength: Long)
}