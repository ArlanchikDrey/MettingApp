
package com.dev.meeting.ui.common

import com.dev.meeting.R
import com.dev.meeting.ui.common.base.BaseRecyclerAdapter


class ImagePagerAdapter(
	private var data: List<String> = emptyList()
): BaseRecyclerAdapter<String>() {

	override fun getItem(position: Int) = data[position]
	override fun getItemCount() = data.size
	override fun getLayoutIdForItem(position: Int) = R.layout.universal_pager_image_container

	fun setData(newData: List<String>) {
		data = newData
		notifyDataSetChanged()
	}
}