
package com.dev.domain.photo

data class PhotoItem(
    var fileName: String = "",
    var fileUrl: String = ""
) {
    companion object {
        const val FACEBOOK_PHOTO_NAME = "FACEBOOK"
        fun FACEBOOK_PHOTO(url: String) = PhotoItem(
            fileName = FACEBOOK_PHOTO_NAME,
            fileUrl = url
        )
    }
}