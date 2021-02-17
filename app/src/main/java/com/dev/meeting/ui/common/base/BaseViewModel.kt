
package com.dev.meeting.ui.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.meeting.core.log.logDebug
import com.dev.meeting.ui.common.errors.MyError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Generic class for ViewModels
 */

abstract class BaseViewModel: ViewModel() {

	internal val error = MutableLiveData<MyError>()
	protected val disposables = CompositeDisposable()
	protected val TAG = "mylogs_${javaClass.simpleName}"

	protected fun mainThread(): Scheduler = AndroidSchedulers.mainThread()

	override fun onCleared() {
		disposables.clear()
		logDebug(TAG, "${javaClass.simpleName} cleared.")
		super.onCleared()
	}
}