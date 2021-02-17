
package com.dev.meeting.ui.settings.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.domain.photo.PhotoItem
import com.dev.meeting.BR
import com.dev.meeting.databinding.ItemSettingsEditInfoPhotoBinding

/**
 * This is the documentation block about the class
 */

class SettingsEditInfoPhotoAdapter(
	private var data: MutableList<PhotoItem> = mutableListOf()
): RecyclerView.Adapter<SettingsEditInfoPhotoAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ItemSettingsEditInfoPhotoBinding.inflate(
			LayoutInflater.from(parent.context), parent, false
		)
		return ViewHolder(binding)
	}
	
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) =
		holder.bind(data[position])

	override fun getItemCount() = data.size
	
	fun removeAt(position: Int) {
		data.removeAt(position)
		notifyItemRemoved(position)
	}
	
	fun setNewData(newData: List<PhotoItem>) {
		data.clear()
		data.addAll(newData)
		notifyDataSetChanged()
	}
	
	private var clickListener: ((PhotoItem, Int) -> Unit)? = null
	
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (PhotoItem, Int) -> Unit) {
		clickListener = listener
	}


	inner class ViewHolder(private val binding: ItemSettingsEditInfoPhotoBinding):
			RecyclerView.ViewHolder(binding.root) {

		fun bind(bindItem: PhotoItem) = binding.run {
			btnDeletePhoto.setOnClickListener { clickListener?.invoke(bindItem, adapterPosition) }
			
			setVariable(BR.bindItem, bindItem)
			executePendingBindings()
		}

	}
}