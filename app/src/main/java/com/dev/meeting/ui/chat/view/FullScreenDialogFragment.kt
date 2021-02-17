
package com.dev.meeting.ui.chat.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import com.dev.meeting.R
import com.dev.meeting.databinding.DialogChatFullScreenImageBinding

/**
 * Dialog to display photo from chat in full screen mode
 */

class FullScreenDialogFragment: DialogFragment() {
	
	private var _binding: DialogChatFullScreenImageBinding? = null
	private val binding: DialogChatFullScreenImageBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private var isHide = false
	private var receivedPhotoUrl = ""

	companion object {
		private const val PHOTO_KEY = "PHOTO_URL"
		fun newInstance(photoUrl: String) = FullScreenDialogFragment().apply {
			arguments = Bundle().apply { putString(PHOTO_KEY, photoUrl) }
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
		arguments?.let {
			receivedPhotoUrl = it.getString(PHOTO_KEY, "")
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = DialogChatFullScreenImageBinding.inflate(inflater, container, false)
		.apply {
			this.photoUrl = receivedPhotoUrl
			_binding = this
			executePendingBindings()
		}
		.root

	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		// Set the content to appear under the system bars so that the
		// content doesn't resize when the system bars hide and show.
		WindowCompat.setDecorFitsSystemWindows(dialog!!.window!!, false)
		ivDialogFullScreen.setOnClickListener { fullScreenCall() }
	}

	//hide bottom navigation to see fullscreen image
	private fun fullScreenCall() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			dialog?.window!!.setDecorFitsSystemWindows(false)
			val controller = dialog?.window!!.insetsController
			if (controller != null) {
				controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
				controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
			}
		} else {
			//for api versions >= 19
			val decorView = dialog?.window!!.decorView
			if (!isHide) {
				decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
						View.SYSTEM_UI_FLAG_FULLSCREEN
				isHide = true
			}
			else {
				decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
				isHide = false
			}
		}

	}


}
