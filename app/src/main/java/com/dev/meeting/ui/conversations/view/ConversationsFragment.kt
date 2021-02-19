
package com.dev.meeting.ui.conversations.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.dev.domain.pairs.MatchedUserItem
import com.dev.meeting.R
import com.dev.meeting.databinding.FragmentConversationsBinding
import com.dev.meeting.ui.common.base.BaseFragment
import com.dev.meeting.ui.common.custom.SwipeToDeleteCallback
import com.dev.meeting.ui.conversations.ConversationsViewModel
import com.dev.meeting.utils.extensions.showToastText
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying active conversations
 */

@AndroidEntryPoint
class ConversationsFragment: BaseFragment<ConversationsViewModel, FragmentConversationsBinding>(
	layoutId = R.layout.fragment_conversations
){
	
	override val mViewModel: ConversationsViewModel by viewModels()

	private val mConversationsAdapter = ConversationsAdapter().apply {
		setLoadNextListener { conversationTimestamp, page ->
			mViewModel.loadNextConversations(conversationTimestamp, page)
		}
		
		setLoadPrevListener { page ->
			mViewModel.loadPrevConversations(page)
		}
		
		setOnItemClickListener { item, position ->
			sharedViewModel.conversationSelected.value = item
			sharedViewModel.matchedUserItemSelected.value = MatchedUserItem(
				baseUserInfo = item.partner,
				conversationId = item.conversationId,
				conversationStarted = item.conversationStarted
			)
			navController.navigate(R.id.action_conversations_to_chatFragment)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		mViewModel.getDeleteConversationStatus().observe(this, {
			if (it) requireContext().showToastText(getString(R.string.toast_text_delete_success))
		})
		
		observeInitConversations()
		observeNextConversations()
		observePrevConversations()
		
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.rvConversationList.run {
		
		setHasFixedSize(true)
		
		adapter = mConversationsAdapter
		layoutManager = LinearLayoutManager(context)
		addItemDecoration(DividerItemDecoration(this.context, VERTICAL))

		val swipeHandler = object : SwipeToDeleteCallback(context) {
			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
				
				val itemPosition = viewHolder.adapterPosition

				MaterialAlertDialogBuilder(context)
					.setCancelable(false)
					.setTitle(getString(R.string.dialog_conversation_delete_title))
					.setMessage(getString(R.string.dialog_conversation_delete_message))
					.setPositiveButton(getString(R.string.dialog_delete_btn_positive_text)) { dialog, _ ->
						mViewModel.deleteConversation(mConversationsAdapter.getItem(itemPosition))
						mConversationsAdapter.removeAt(itemPosition)
						
					}
					.setNegativeButton(getString(R.string.dialog_delete_btn_negative_text)) { dialog, _ ->
						//dismiss animation
						mConversationsAdapter.notifyItemChanged(itemPosition)
					}
					.create()
					.show()
			}
		}
		val itemTouchHelper = ItemTouchHelper(swipeHandler)
		itemTouchHelper.attachToRecyclerView(this)


	}

	private fun observeInitConversations() = mViewModel.initConversations.observe(this, {
		mConversationsAdapter.setNewData(it)
	})
	
	private fun observeNextConversations() = mViewModel.nextConversations.observe(this, {
		mConversationsAdapter.insertNextData(it)
	})
	
	private fun observePrevConversations() = mViewModel.prevConversations.observe(this, {
		mConversationsAdapter.insertPreviousData(it)
	})

	override fun onBackPressed() {
		super.onBackPressed()
		onCloseActivity()
	}
}