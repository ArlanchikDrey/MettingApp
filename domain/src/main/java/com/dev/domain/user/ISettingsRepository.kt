
package com.dev.domain.user

import com.dev.domain.photo.PhotoItem
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

/**
 * Repository for editing information about yourself
 */

interface ISettingsRepository {
	
	fun uploadUserProfilePhoto(
		userItem: UserItem,
		photoUri: String
	): Observable<Array<PhotoItem>>
	
	fun deletePhoto(userItem: UserItem, photoItem: PhotoItem, isMainPhotoDeleting: Boolean): Completable
	
	fun deleteMyself(user: UserItem): Completable
	
}