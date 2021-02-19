
package com.dev.meeting.ui.common.base

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.dev.meeting.BR
import com.dev.meeting.R
import com.dev.meeting.ui.SharedViewModel
import com.dev.meeting.utils.extensions.showErrorDialog
import com.dev.meeting.utils.extensions.showToastText

/**
 * Generic Fragment class
 */

abstract class BaseFragment<T: ViewModel, Binding: ViewDataBinding>(
	@LayoutRes private val layoutId: Int
) : Fragment() {
	
	private var _binding: Binding? = null
	
	protected val binding: Binding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)

	protected lateinit var navController: NavController
	protected val TAG = "mylogs_${javaClass.simpleName}"

	protected val sharedViewModel: SharedViewModel by activityViewModels()


	protected abstract val mViewModel: T?

	private lateinit var callback: OnBackPressedCallback

	private var exit = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		navController = findNavController()
		setBackButtonDispatcher()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = DataBindingUtil.inflate<Binding>(inflater, layoutId, container, false).apply {
		lifecycleOwner = viewLifecycleOwner
		setVariable(BR.viewModel, mViewModel)
		_binding = this
	}.root
	

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		(mViewModel as BaseViewModel?)?.showErrorDialog(this, context)
	}
	
	/**
	 * Adding BackButtonDispatcher callback to activity
	 */
	private fun setBackButtonDispatcher() {
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				onBackPressed()
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	/**
	 * Override this method into your fragment to handle backButton
	 */
	open fun onBackPressed() {}
	
	override fun onDestroy() {
		_binding = null
		super.onDestroy()
	}

	open fun onCloseActivity() {
		if (exit) {
			requireActivity().finish()
		} else {
			with(requireContext()){
				showToastText(resources.getString(R.string.toast_text_exit))
			}
			exit = true
			Handler().postDelayed({ exit = false }, 3 * 1000)
		}
	}
	
}