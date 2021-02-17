
package com.dev.meeting.ui.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.dev.domain.pairs.MatchedUserItem
import com.dev.domain.user.IUserRepository
import com.dev.domain.user.data.BaseUserInfo
import com.dev.domain.user.data.ReportType
import com.dev.domain.user.data.UserItem
import com.dev.meeting.ui.MainActivity
import com.dev.meeting.ui.common.base.BaseViewModel
import com.dev.meeting.ui.common.errors.ErrorType
import com.dev.meeting.ui.common.errors.MyError

/**
 * This is the documentation block about the class
 */

class RemoteRepoViewModel @ViewModelInject constructor(
	private val repo: IUserRepository
) : BaseViewModel() {
	
	val reportSubmittingStatus: MutableLiveData<Boolean> = MutableLiveData()
	val unmatchStatus: MutableLiveData<Boolean> = MutableLiveData()
	
	val retrievedUserItem: MutableLiveData<UserItem> = MutableLiveData()
	

	fun deleteMatchedUser(matchedUser: MatchedUserItem) {
		disposables.add(repo.deleteMatchedUser(MainActivity.currentUser!!, matchedUser)
            .observeOn(mainThread())
            .subscribe(
	            { unmatchStatus.value = true },
	            { error.value = MyError(ErrorType.DELETING, it) }
            )
		)
	}

	fun getRequestedUserInfo(baseUserInfo: BaseUserInfo) {
		disposables.add(repo.getRequestedUserItem(baseUserInfo)
            .observeOn(mainThread())
            .subscribe(
	            { retrievedUserItem.value = it },
	            { error.value = MyError(ErrorType.LOADING, it) }
            )
		)
	}

	fun submitReport(reportType: ReportType, baseUserInfo: BaseUserInfo) {
		disposables.add(repo.submitReport(reportType, baseUserInfo)
            .observeOn(mainThread())
            .subscribe(
	            { reportSubmittingStatus.value = true },
	            { error.value = MyError(ErrorType.SUBMITING, it) }
            )
		)
	}
	
}