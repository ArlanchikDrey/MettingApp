/*
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.dev.meeting.ui.pairs.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.dev.domain.conversations.ConversationItem
import com.dev.meeting.R
import com.dev.meeting.core.log.logWtf
import com.dev.meeting.databinding.FragmentPairsBinding
import com.dev.meeting.ui.common.base.BaseFragment
import com.dev.meeting.ui.common.custom.GridItemDecoration
import com.dev.meeting.ui.pairs.PairsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment to display your active pairs
 */

@AndroidEntryPoint
class PairsFragment: BaseFragment<PairsViewModel, FragmentPairsBinding>(
	layoutId = R.layout.fragment_pairs
) {
	
	override val mViewModel: PairsViewModel by viewModels()
	
	private val mPairsAdapter = PairsAdapter().apply {
		
		setLoadNextListener { matchDate, page ->
			logWtf(TAG, "$page")
			mViewModel.loadNextMatchedUsers(matchDate, page)
		}
		
		setLoadPrevListener { page ->
			logWtf(TAG, "$page")
			mViewModel.loadPrevMatchedUsers(page)
		}
		
		setOnItemClickListener { item, position ->
			sharedViewModel.matchedUserItemSelected.value = item
			sharedViewModel.conversationSelected.value = ConversationItem(
				partner = item.baseUserInfo,
				conversationId = item.conversationId,
				conversationStarted = item.conversationStarted
			)
			navController.navigate(R.id.action_pairs_to_profileFragment)
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		observeInitPairs()
		observeNextPairs()
		observePrevPairs()
		
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.rvPairList.run {
		setHasFixedSize(true)
		
		adapter = mPairsAdapter
		layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
		addItemDecoration(GridItemDecoration())
	}
	
	
	private fun observeInitPairs() = mViewModel.initPairs.observe(this, {
		mPairsAdapter.setNewData(it)
	})
	
	private fun observeNextPairs() = mViewModel.nextPairs.observe(this, {
		mPairsAdapter.insertNextData(it)
	})
	
	private fun observePrevPairs() = mViewModel.prevPairs.observe(this, {
		mPairsAdapter.insertPreviousData(it)
	})
}