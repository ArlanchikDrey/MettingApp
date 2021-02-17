
package com.dev.meeting.core.permissions

import android.Manifest
import com.dev.meeting.R
import com.dev.meeting.core.permissions.AppPermission.PermissionCode.*

/**
 * Wrapper for app permissions
 */

sealed class AppPermission(
	val permissionsList: Array<String>,
	val permissionCode: PermissionCode,
	val deniedMessageId: Int,
	val explanationMessageId: Int
) {
	
	enum class PermissionCode(val code: Int) {
		REQUEST_CODE_CAMERA(1),
		REQUEST_CODE_GALLERY(2),
		REQUEST_CODE_LOCATION(3)
	}


	/** CAMERA PERMISSIONS */
	object CAMERA : AppPermission(
		arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
		REQUEST_CODE_CAMERA,
		R.string.permission_camera_denied,
		R.string.permission_camera_explanation
	)

	/** READ/WRITE TO STORAGE PERMISSIONS */
	object GALLERY : AppPermission(
		arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
		REQUEST_CODE_GALLERY,
		R.string.permission_read_ext_storage_denied,
		R.string.permission_read_ext_storage_explanation
	)
	
	/** Access to location */
	object LOCATION : AppPermission(
		arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
		REQUEST_CODE_LOCATION,
		R.string.permission_location_denied,
		R.string.permission_location_explanation
	)

}
