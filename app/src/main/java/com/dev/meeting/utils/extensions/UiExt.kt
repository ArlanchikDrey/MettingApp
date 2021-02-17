

package com.dev.meeting.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

/**
 * Show the view (visibility = View.VISIBLE)
 */
fun View.visible(): View {
	if (visibility != View.VISIBLE) visibility = View.VISIBLE
	return this
}

fun View.visibleWithAnimation(delay: Int = 0): View {
	if (visibility != View.VISIBLE) {
		//check if previous state was GONE to prevent unexpected crash "No such view"
		if (visibility == View.GONE) visibility = View.INVISIBLE
		clearAnimation()
		alpha = 0.0f
		visibility = View.VISIBLE
		animate().alpha(1.0f).setDuration(delay.toLong()).setListener(null)
	}
	return this
}

/**
 * Hide the view (visibility = [View.INVISIBLE])
 */
fun View.invisible(): View {
	if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
	return this
}

fun View.invisibleWithAnimation(delay: Int = 0): View {
	if (visibility != View.INVISIBLE) {
		clearAnimation()
		animate().alpha(0.0f).setDuration(delay.toLong())
			.setListener(object: AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					visibility = View.INVISIBLE
				}
			})
		
	}
	return this
}

/**
 * Remove the view (visibility = View.GONE)
 */
fun View.gone(): View {
	if (visibility != View.GONE) visibility = View.GONE
	return this
}

fun View.goneWithAnimation(delay: Int = 0): View {
	if (visibility != View.GONE) {
		clearAnimation()
		animate().alpha(0.0f).setDuration(delay.toLong())
			.setListener(object: AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					clearAnimation()
					visibility = View.GONE
				}
			})
	}
	return this
}
