
package com.dev.meeting.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dev.domain.photo.PhotoItem
import com.dev.meeting.R
import com.dev.meeting.databinding.ItemSettingsPhotoBinding
import com.dev.meeting.ui.common.base.BaseRecyclerAdapter

/**
 * better to use notifyItemInserted instead of notifyDataSetChanged()
 * but bug with custom layout manager exists
 */

class SettingsUserPhotoAdapter(
	private var data: List<PhotoItem> = emptyList()
): BaseRecyclerAdapter<PhotoItem>(){
	
	override fun getItem(position: Int): PhotoItem = data[position]
	override fun getItemCount() = data.size
	override fun getLayoutIdForItem(position: Int) = R.layout.item_settings_photo
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PhotoItem> {
		val binding = ItemSettingsPhotoBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		binding.root.post {
			binding.root.layoutParams.width = parent.width / 2
			binding.root.requestLayout()
		}
		return BaseViewHolder(binding)
	}
	
	fun newAdded(newData: PhotoItem) {
		data = data.plus(newData)
		notifyDataSetChanged()
	}
	
	fun setData(newData: List<PhotoItem>) {
		data = newData
		notifyDataSetChanged()
	}
}