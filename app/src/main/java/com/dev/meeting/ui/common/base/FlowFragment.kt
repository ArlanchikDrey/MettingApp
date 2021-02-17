
package com.dev.meeting.ui.common.base

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * Flow fragment correspond to host fragment switching by bottom navigation
 */

abstract class FlowFragment<VM: BaseViewModel, Binding: ViewDataBinding>(
	@LayoutRes layoutId: Int
) : BaseFragment<VM, Binding>(layoutId = layoutId)