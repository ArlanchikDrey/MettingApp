
package com.dev.meeting.ui.chat.view


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dev.domain.chat.MessageItem
import com.dev.domain.photo.PhotoItem
import com.dev.meeting.R
import com.dev.meeting.core.glide.GlideApp
import com.dev.meeting.ui.chat.view.ChatAdapter.ChatViewHolder

class ChatAdapter(
	private val data: MutableList<MessageItem> = mutableListOf()
): RecyclerView.Adapter<ChatViewHolder>() {
	
	private var userId = ""

	companion object {
		private const val RIGHT_MSG = 0
		private const val LEFT_MSG = 1
		private const val RIGHT_MSG_IMG = 2
		private const val LEFT_MSG_IMG = 3
		
		private const val MESSAGES_PER_LOAD = 20
	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		if (viewType == RIGHT_MSG || viewType == RIGHT_MSG_IMG) ChatViewHolder(
			LayoutInflater.from(parent.context).inflate(
				R.layout.fragment_chat_item_right,
				parent,
				false
			)
		)
		else ChatViewHolder(
			LayoutInflater.from(parent.context).inflate(
				R.layout.fragment_chat_item_left,
				parent,
				false
			)
		)

	override fun onBindViewHolder(viewHolder: ChatViewHolder, position: Int) {
		viewHolder.setMessageType(getItemViewType(position))
		viewHolder.bind(data[position])
	}

	override fun getItemViewType(position: Int): Int {
		val message = data[position]
		return if (message.photoItem != null) {
			if (message.sender.userId == userId) RIGHT_MSG_IMG
			else LEFT_MSG_IMG
		}
		else {
			if (message.sender.userId == userId) RIGHT_MSG
			else LEFT_MSG
		}
	}

	override fun getItemCount() = data.size

	fun newMessage(message: MessageItem) {
		data.add(0, message)
		notifyItemInserted(0)
	}
	
	//insert paginated messages list
	fun insertPrev(prevData: List<MessageItem>) {
		val prevCursor = data.size - 1
		data.addAll(prevData)
		notifyItemRangeChanged(prevCursor, prevData.size)
	}

	fun setCurrentUserId(id: String) { userId = id }

	fun setNewData(newData: List<MessageItem>) {
		data.clear()
		data.addAll(newData)
		notifyDataSetChanged()
	}
	
	private var loadPrevListener: ((MessageItem) -> Unit)? = null
	fun setLoadPrevListener(listener: (MessageItem) -> Unit) { loadPrevListener = listener }

	
	private var clickListener: ((View, Int, PhotoItem?) -> Unit)? = null
	
	// allows clicks events on attached photo
	fun setOnAttachedPhotoClickListener(listener: (View, Int, PhotoItem?) -> Unit) {
		clickListener = listener
	}

	inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view){

		private val tvTextMessage: TextView = itemView.findViewById(R.id.tvChatMessageText)
		private val ivChatPhoto: ImageView = itemView.findViewById(R.id.ivChatMessagePhoto)

		init {
			ivChatPhoto.setOnClickListener {
				clickListener?.invoke(it, adapterPosition, data[adapterPosition].photoItem)
			}
		}

		fun setMessageType(messageType: Int) {
			when (messageType) {
				RIGHT_MSG -> ivChatPhoto.visibility = View.GONE
				LEFT_MSG -> ivChatPhoto.visibility = View.GONE
				RIGHT_MSG_IMG -> tvTextMessage.visibility = View.GONE
				LEFT_MSG_IMG -> tvTextMessage.visibility = View.GONE
			}
		}

		fun bind(bindItem: MessageItem) {
			
			if (adapterPosition == data.size - 5 && data.size >= MESSAGES_PER_LOAD)
				loadPrevListener?.invoke(data.last())
			
			setTextMessage(bindItem.text)
			bindItem.photoItem?.let {
				if (ivChatPhoto.visibility != View.GONE) setIvChatPhoto(it.fileUrl)
			}
		}

		/* sets text message in TxtView binded layout */
		private fun setTextMessage(message: String?) { tvTextMessage.text = message }

		/* set photo that user sends in chat */
		private fun setIvChatPhoto(url: String) {
			if (url.isNotEmpty()) {
				GlideApp.with(ivChatPhoto.context)
					.load(url)
					.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
					.into(ivChatPhoto)
			}
		}

	}

}
