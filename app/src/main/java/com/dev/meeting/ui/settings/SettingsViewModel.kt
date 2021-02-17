
package com.dev.meeting.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.dev.domain.photo.PhotoItem
import com.dev.domain.user.ISettingsRepository
import com.dev.meeting.ui.MainActivity
import com.dev.meeting.ui.common.base.BaseViewModel
import com.dev.meeting.ui.common.errors.ErrorType
import com.dev.meeting.ui.common.errors.ErrorType.DELETING
import com.dev.meeting.ui.common.errors.MyError
import com.dev.meeting.ui.settings.SettingsViewModel.DeletingStatus.IN_PROGRESS

/**
 * ViewModel to edit user info, basically allows to start add/delete photo or delete account
 */

class SettingsViewModel @ViewModelInject constructor(
	private val repo: ISettingsRepository
): BaseViewModel() {
	
	
	val selfDeletingStatus: MutableLiveData<DeletingStatus> = MutableLiveData()
	
	val newPhoto = MutableLiveData<PhotoItem>()
	
	fun deleteMyAccount() {
		disposables.add(repo.deleteMyself(MainActivity.currentUser!!)
			.doOnSubscribe { selfDeletingStatus.postValue(IN_PROGRESS) }
            .observeOn(mainThread())
            .subscribe(
                { selfDeletingStatus.value = DeletingStatus.COMPLETED },
                {
	                selfDeletingStatus.value = DeletingStatus.FAILURE
	                error.value = MyError(DELETING, it)
                }
            )
		)
	}
	
	fun deletePhoto(photoItem: PhotoItem, isMainPhotoDeleting: Boolean) {
		disposables.add(repo.deletePhoto(MainActivity.currentUser!!, photoItem, isMainPhotoDeleting)
            .subscribe(
                {
	                //make new list by deleting requested photo
	                val newUrls = MainActivity.currentUser!!.photoURLs.minus(photoItem)
	
	                //update current user with new photo list
	                MainActivity.currentUser = MainActivity.currentUser!!.copy(
		                photoURLs = newUrls
	                )
	
	                //also update mainPhotoUrl if such delete operation was occured
	                if (isMainPhotoDeleting) {
		                val newBaseUserInfo = MainActivity.currentUser!!.baseUserInfo.copy(
			                mainPhotoUrl = newUrls[0].fileUrl
		                )
		                MainActivity.currentUser = MainActivity.currentUser!!.copy(
			                baseUserInfo = newBaseUserInfo
		                )
	                }
                },
                { error.value = MyError(DELETING, it) }
            )
		)
	}
	
	fun uploadUserProfilePhoto(photoUri: String) {
		disposables.add(repo.uploadUserProfilePhoto(MainActivity.currentUser!!, photoUri)
            .observeOn(mainThread())
            .subscribe(
                {
	                if (it.isNotEmpty()) {
	                	newPhoto.postValue(it.first())
		                
		                val newPhotos = MainActivity.currentUser!!.photoURLs.plus(it.first())
		                
		                //update current user with new photo list
		                MainActivity.currentUser = MainActivity.currentUser!!.copy(photoURLs = newPhotos)
	                }
                }
                ,
                { error.value = MyError(ErrorType.UPLOADING, it) }
            )
		)
	}
	
	
	enum class DeletingStatus { IN_PROGRESS, COMPLETED, FAILURE }
	
}