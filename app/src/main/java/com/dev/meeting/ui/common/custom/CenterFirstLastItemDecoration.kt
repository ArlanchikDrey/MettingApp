
package com.dev.meeting.ui.common.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * Decorator for center first and last item at RecyclerView
 */

class CenterFirstLastItemDecoration(private val childWidth: Int): RecyclerView.ItemDecoration() {

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		val position = parent.getChildViewHolder(view).adapterPosition
		if (position == 0 || position == state.itemCount - 1) {
			val displayWidth = parent.context.resources.displayMetrics.widthPixels
			
			val padding = if (childWidth > displayWidth / 2) (displayWidth / 2f - childWidth / 2f).roundToInt()
			else childWidth / 2
			if (position == 0) { outRect.left = padding }
			else { outRect.right = padding }
		}
	}

}