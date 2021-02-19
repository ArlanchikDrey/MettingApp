
package com.dev.meeting.ui.cards.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dev.domain.user.data.UserItem
import com.dev.meeting.R
import com.dev.meeting.databinding.FragmentCardsBinding
import com.dev.meeting.ui.cards.CardsViewModel
import com.dev.meeting.ui.cards.CardsViewModel.SwipeAction.*
import com.dev.meeting.ui.common.ImagePagerAdapter
import com.dev.meeting.ui.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardsFragment: BaseFragment<CardsViewModel, FragmentCardsBinding>(
	layoutId = R.layout.fragment_cards
) {
	
	override val mViewModel: CardsViewModel by viewModels()
	
	private val mTopCardImagePagerAdapter = ImagePagerAdapter()
	private val mBottomCardImagePagerAdapter = ImagePagerAdapter()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		observeTopCard()
		observeBottomCard()
		observeMatch()
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
			override fun onTransitionTrigger(layout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {}
			override fun onTransitionStarted(layout: MotionLayout, start: Int, end: Int) {}
			override fun onTransitionChange(layout: MotionLayout, start: Int, end: Int, position: Float) {}
			
			override fun onTransitionCompleted(layout: MotionLayout, currentId: Int) {
				when (currentId) {
					R.id.topOffScreenSkip -> mViewModel.swipeTop(SKIP)
					R.id.topOffScreenLike -> mViewModel.swipeTop(LIKE)
					
					R.id.bottomOffScreenSkip -> mViewModel.swipeBottom(SKIP)
					R.id.bottomOffScreenLike -> mViewModel.swipeBottom(LIKE)
				}
			}
		})
	}
	
	/** top card setup ui*/
	private fun observeTopCard() = mViewModel.topCard.observe(this, { setTopCard(it) })
	private fun setTopCard(userItem: UserItem?) = binding.topCard.run {
		if (userItem != null) root.run {
			
			mTopCardImagePagerAdapter.setData(userItem.photoURLs.map { it.fileUrl })
			
			vpCardPhotos.apply {
				adapter = mTopCardImagePagerAdapter
				isUserInputEnabled = false
			}
			
			TabLayoutMediator(tlCardPhotosIndicator, vpCardPhotos) { _: TabLayout.Tab, _: Int ->
				//do nothing
			}.attach()
			
			nextImage.setOnClickListener {
				if (vpCardPhotos.currentItem != vpCardPhotos.itemDecorationCount - 1) vpCardPhotos.currentItem++
			}
			previousImage.setOnClickListener {
				if (vpCardPhotos.currentItem != 0) vpCardPhotos.currentItem--
			}
			
			tvCardUserName.text = getString(R.string.name_age_formatter).format(
				userItem.baseUserInfo.name, userItem.baseUserInfo.age
			)
		}
	}
	
	
	/** bottom card setup ui*/
	private fun observeBottomCard() = mViewModel.bottomCard.observe(this, { setBottomCard(it) })
	private fun setBottomCard(userItem: UserItem?) = binding.bottomCard.run {
		if (userItem != null) {
			
			mBottomCardImagePagerAdapter.setData(userItem.photoURLs.map { it.fileUrl })
			
			vpCardPhotos.apply {
				adapter = mBottomCardImagePagerAdapter
				isUserInputEnabled = false
			}
			
			TabLayoutMediator(tlCardPhotosIndicator, vpCardPhotos) { _: TabLayout.Tab, _: Int ->
				//do nothing
			}.attach()
			
			nextImage.setOnClickListener {
				if (vpCardPhotos.currentItem != vpCardPhotos.itemDecorationCount - 1) vpCardPhotos.currentItem++
			}
			previousImage.setOnClickListener {
				if (vpCardPhotos.currentItem != 0) vpCardPhotos.currentItem--
			}
			
			tvCardUserName.text = getString(R.string.name_age_formatter).format(
				userItem.baseUserInfo.name, userItem.baseUserInfo.age
			)
			
		}
	}
	
	private fun observeMatch() = mViewModel.showMatchDialog.observe(this, {
		if (it != null) showMatchDialog(it)
	})
	private fun showMatchDialog(userItem: UserItem) = MatchDialogFragment.newInstance(
		userItem.baseUserInfo.name, userItem.baseUserInfo.mainPhotoUrl
	).show(childFragmentManager, MatchDialogFragment::class.java.canonicalName)

	override fun onBackPressed() {
		super.onBackPressed()
		onCloseActivity()
	}
	
}
