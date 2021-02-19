
package com.dev.meeting.ui.conversations.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dev.domain.conversations.ConversationItem
import com.dev.meeting.BR
import com.dev.meeting.databinding.ItemConversationBinding
import java.util.*

class ConversationsAdapter(
	private var data: MutableList<ConversationItem> = mutableListOf()
): RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {
	
	private companion object{
		private const val FIRST_POS = 0
		private const val ITEMS_PER_PAGE = 20
		private const val OPTIMAL_ITEMS_COUNT = ITEMS_PER_PAGE * 2
	}
	private var startPos = 0
	private var itemsLoaded = 0
	private var pageTop = 0
		set(value) {
			field = if (value < 0) 0
			else value
		}
	private var pageBottom = 0
		set(value) {
			if (value > 1) pageTop = value - 1
			field = value
		}
	private var isLastPage = false
	
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
		ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
	)
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])
	
	override fun getItemCount() = data.size
	
	fun getItem(position: Int) = data[position]
	
	private var mClickListener: ((ConversationItem, Int) -> Unit)? = null
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (ConversationItem, Int) -> Unit) { mClickListener = listener }
	
	
	private var loadPrevListener: ((Int) -> Unit)? = null
	private var loadNextListener: ((Date, Int) -> Unit)? = null
	
	fun setLoadPrevListener(listener: (Int) -> Unit) { loadPrevListener = listener }
	fun setLoadNextListener(listener: (Date, Int) -> Unit) { loadNextListener = listener }

	fun setNewData(newData: List<ConversationItem>) {
		data.clear()
		data.addAll(newData)
		notifyDataSetChanged()
		//notifyItemRangeChanged(0,data.size - 1)
	}
	
	fun insertPreviousData(topData: List<ConversationItem>) {
		//update offsets
		isLastPage = false
		if (topData.size == ITEMS_PER_PAGE) pageBottom = pageTop
		
		data.addAll(FIRST_POS, topData)
		notifyItemRangeInserted(FIRST_POS, topData.size)
		
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = data.size - OPTIMAL_ITEMS_COUNT
			data = data.dropLast(shouldBeRemovedCount).toMutableList()
			itemsLoaded -= shouldBeRemovedCount
			notifyItemRangeRemoved((data.size - 1), shouldBeRemovedCount)
		}
	}
	
	
	fun insertNextData(bottomData: List<ConversationItem>) {
		//update offsets
		pageBottom++
		isLastPage = bottomData.size != ITEMS_PER_PAGE
		
		startPos = data.size
		data.addAll(bottomData)
		itemsLoaded += bottomData.size
		notifyItemRangeInserted(startPos, bottomData.size)
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			data = data.drop(ITEMS_PER_PAGE).toMutableList()
			notifyItemRangeRemoved(FIRST_POS, ITEMS_PER_PAGE)
		}
	}

	fun removeAt(position: Int) {
		data.removeAt(position)
		notifyItemRemoved(position)
	}
	
	inner class ViewHolder(private val binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		init {
			mClickListener?.let { mClickListener ->
				itemView.setOnClickListener {
					mClickListener.invoke(data[adapterPosition], adapterPosition)
				}
			}
		}
		
		fun bind(item: ConversationItem) {
			if (adapterPosition > 9 && adapterPosition == (data.size - 5) && !isLastPage)
				loadNextListener?.invoke(data.last().lastMessageTimestamp!!, pageBottom + 1)
			
			if (itemsLoaded >= data.size && adapterPosition == 1)
				loadPrevListener?.invoke(pageTop - 1)
			
			
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
}