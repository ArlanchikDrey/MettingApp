

package com.dev.meeting.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.dev.meeting.ui.common.base.BaseViewModel


fun Context.showToastText(text: String) =
	Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observe(lifecycleOwner, object : Observer<T> {
		override fun onChanged(t: T?) {
			observer.onChanged(t)
			removeObserver(this)
		}
	})
}

/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard(inputViewFocused: View? = null): Boolean {
	try {
		val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		return inputMethodManager.hideSoftInputFromWindow(
			applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS
		)
	} catch (ignored: RuntimeException) { }
	finally { inputViewFocused?.clearFocus() }
	return false
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun BaseViewModel.showErrorDialog(lifecycleOwner: LifecycleOwner, context: Context?) {
	this.error.observe(lifecycleOwner, { myError ->
		context?.let {
			MaterialAlertDialogBuilder(it)
				.setTitle(myError.errorType.name)
				.setMessage(myError.getErrorMessage())
				.setPositiveButton("OK", null)
				.create()
				.show()
		}
	})
}

