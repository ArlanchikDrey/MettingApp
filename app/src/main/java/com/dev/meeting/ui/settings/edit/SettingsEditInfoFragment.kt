
package com.dev.meeting.ui.settings.edit

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.dev.domain.user.data.Gender
import com.dev.domain.user.data.Gender.*
import com.dev.meeting.R
import com.dev.meeting.databinding.FragmentSettingsEditInfoBinding
import com.dev.meeting.ui.MainActivity
import com.dev.meeting.ui.common.base.BaseFragment
import com.dev.meeting.ui.common.custom.GridItemDecoration
import com.dev.meeting.ui.settings.SettingsViewModel
import com.dev.meeting.utils.extensions.hideKeyboard
import com.dev.meeting.utils.extensions.showToastText
import dagger.hilt.android.AndroidEntryPoint

/**
 * This fragment allow you to edit your profile
 */

@AndroidEntryPoint
class SettingsEditInfoFragment: BaseFragment<SettingsViewModel, FragmentSettingsEditInfoBinding>(
	layoutId = R.layout.fragment_settings_edit_info
) {
	
	override val mViewModel: SettingsViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	
	private val mEditorPhotoAdapter = SettingsEditInfoPhotoAdapter()

	private var newName: String? = null
	private var newAge: Int? = null
	private var newGender: Gender? = null
	private var newDescription: String? = null

	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		
		initProfile()
		
		//touch event guarantee that if user want to scroll or touch outside of edit box
		//keyboard hide and editText focus clear
		root.setOnTouchListener { v, _ ->
			v.performClick()
			v.hideKeyboard(binding.edSettingsEditDescription)
			return@setOnTouchListener true
		}
		
		rvSettingsEditPhotos.run {
			
			layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
			adapter = mEditorPhotoAdapter.apply {
				setOnItemClickListener { item , position ->
					if (mEditorPhotoAdapter.itemCount > 1) {
						val isMainPhotoDeleting = item.fileUrl == MainActivity.currentUser!!.baseUserInfo.mainPhotoUrl
						
						mViewModel.deletePhoto(item, isMainPhotoDeleting)
						mEditorPhotoAdapter.removeAt(position)
					}
					else context.showToastText(getString(R.string.toast_text_at_least_1_photo_required))
					
				}
			}
			addItemDecoration(GridItemDecoration())
		}

		
		toggleButtonSettingsEditGender.addOnButtonCheckedListener { _, checkedId, isChecked ->
			when {
				checkedId == binding.btnSettingsEditGenderMale.id && isChecked -> { newGender = MALE }
				checkedId == binding.btnSettingsEditGenderFemale.id && isChecked -> { newGender = FEMALE }
			}
		}
		
		btnSettingsEditSave.setOnClickListener {
			val item = MainActivity.currentUser!!.copy(
				baseUserInfo = MainActivity.currentUser!!.baseUserInfo.copy(
					name = newName ?: MainActivity.currentUser!!.baseUserInfo.name,
					age = newAge ?: MainActivity.currentUser!!.baseUserInfo.age,
					gender = newGender ?: MainActivity.currentUser!!.baseUserInfo.gender
				),
				aboutText = newDescription ?: MainActivity.currentUser!!.aboutText
			)
			sharedViewModel.updateUser(
				item
			)
		}
		btnSettingsEditDelete.setOnClickListener { showDialogDeleteAttention() }
	}


	private fun initProfile() {
		if (MainActivity.currentUser!!.baseUserInfo.gender == MALE)
			binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderMale)
		else binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderFemale)
		
		mEditorPhotoAdapter.setNewData(MainActivity.currentUser!!.photoURLs)

		changerNameSetup()
		changerAgeSetup()
		changerDescriptionSetup()

	}

	private fun changerNameSetup() = binding.edSettingsEditName.run {
		setText(MainActivity.currentUser!!.baseUserInfo.name)
		
		doAfterTextChanged {
			when {
				it.isNullOrBlank() -> {
					binding.layoutSettingsEditName.error = getString(R.string.text_empty_error)
					binding.btnSettingsEditSave.isEnabled = false
				}
				
				else -> {
					binding.layoutSettingsEditName.error = ""
					newName = it.toString().trim()
					binding.btnSettingsEditSave.isEnabled = true
				}
			}
		}
	}
	
	private fun changerAgeSetup() = binding.sliderSettingsEditAge.run {
		value = MainActivity.currentUser!!.baseUserInfo.age.toFloat()
		
		addOnChangeListener { _, value, _ ->
			newAge = value.toInt()
			binding.tvSettingsEditAge.text =
				getString(R.string.fragment_settings_age_formatter).format(newAge)
		}
	}
	
	private fun changerDescriptionSetup() = binding.edSettingsEditDescription.run {
		setText(MainActivity.currentUser!!.aboutText)
		
		doOnTextChanged { text, start, before, count ->
			newDescription = text.toString().trim()
		}

	}

	private fun showDialogDeleteAttention() = MaterialAlertDialogBuilder(requireContext())
		.setTitle(R.string.dialog_profile_delete_title)
		.setMessage(R.string.dialog_profile_delete_message)
		.setPositiveButton(R.string.dialog_delete_btn_positive_text) { _, _ -> mViewModel.deleteMyAccount() }
		.setNegativeButton(R.string.dialog_delete_btn_negative_text, null)
		.create()
		.show()

	override fun onBackPressed() {
		super.onBackPressed()
		navController.navigateUp()
	}

}