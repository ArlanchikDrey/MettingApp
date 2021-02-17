
package com.dev.meeting.ui.common.custom.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Custom parent to intercept all touches events to prevent being clicked any of view that is
 * showing below this viewGroup
 */

class TouchLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
    
}