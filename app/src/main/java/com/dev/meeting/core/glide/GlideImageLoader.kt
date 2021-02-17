
package com.dev.meeting.core.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target


/**
 * This is the documentation block about the class
 */

class GlideImageLoader(private val mImageView: ImageView,
                       private val mProgressBar: ProgressBar?) {

	fun load(url: String?, options: RequestOptions?) {
		if (options == null) return

		onConnecting()

		DispatchingProgressManager.expect(url, object : UIonProgressListener {

			override val granularityPercentage: Float
				get() = 1.0f

			override fun onProgress(bytesRead: Long, expectedLength: Long) {
				if (mProgressBar != null) {
					mProgressBar.progress = (100 * bytesRead / expectedLength).toInt()
				}
			}
		})

		GlideApp.with(mImageView.context)
			.load(url)
			.apply(options)
			.listener(object : RequestListener<Drawable> {

				override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
				                          isFirstResource: Boolean): Boolean {
					DispatchingProgressManager.forget(url)
					onFinished()
					return false
				}

				override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
				                             dataSource: DataSource?, isFirstResource: Boolean): Boolean {

					DispatchingProgressManager.forget(url)
					onFinished()
					return false
				}
			})
			.into(mImageView)
	}


	private fun onConnecting() {
		if (mProgressBar != null) mProgressBar.visibility = View.VISIBLE
	}

	private fun onFinished() {
		if (mProgressBar != null) {
			mProgressBar.visibility = View.GONE
			mImageView.visibility = View.VISIBLE
		}
	}
}