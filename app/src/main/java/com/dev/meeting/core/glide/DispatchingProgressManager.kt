
package com.dev.meeting.core.glide

import android.os.Handler
import android.os.Looper
import okhttp3.HttpUrl

class DispatchingProgressManager: ResponseProgressListener {

	companion object {
		private val PROGRESSES = HashMap<String?, Long>()
		private val LISTENERS = HashMap<String?, UIonProgressListener>()

		internal fun expect(url: String?, listener: UIonProgressListener) {
			LISTENERS[url] = listener
		}

		internal fun forget(url: String?) {
			LISTENERS.remove(url)
			PROGRESSES.remove(url)
		}
	}

	private val handler: Handler = Handler(Looper.getMainLooper())

	override fun update(url: HttpUrl, bytesRead: Long, contentLength: Long) {
		val key = url.toString()
		val listener = LISTENERS[key] ?: return
		if (contentLength <= bytesRead) {
			forget(key)
		}
		if (needsDispatch(key, bytesRead, contentLength,
		                  listener.granularityPercentage)) {
			handler.post { listener.onProgress(bytesRead, contentLength) }
		}
	}

	private fun needsDispatch(key: String, current: Long, total: Long, granularity: Float): Boolean {
		if (granularity == 0f || current == 0L || total == current) {
			return true
		}
		val percent = 100f * current / total
		val currentProgress = (percent / granularity).toLong()
		val lastProgress = PROGRESSES[key]
		return if (lastProgress == null || currentProgress != lastProgress) {
			PROGRESSES[key] = currentProgress
			true
		} else {
			false
		}
	}
}