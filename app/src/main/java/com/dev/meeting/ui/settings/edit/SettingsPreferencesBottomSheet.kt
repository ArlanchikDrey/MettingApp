
package com.dev.meeting.ui.settings.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.dev.domain.user.data.SelectionPreferences.PreferredGender
import com.dev.domain.user.data.SelectionPreferences.PreferredGender.*
import com.dev.domain.user.data.UserItem
import com.dev.meeting.R
import com.dev.meeting.databinding.BtmSheetSettingsPreferencesBinding
import com.dev.meeting.ui.MainActivity
import com.dev.meeting.ui.SharedViewModel

class SettingsPreferencesBottomSheet : BottomSheetDialogFragment() {
	
	private var _binding: BtmSheetSettingsPreferencesBinding? = null
	private val binding: BtmSheetSettingsPreferencesBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private val sharedViewModel: SharedViewModel by activityViewModels()
	
	
	private var newPreferredGender: PreferredGender? = null
	private var newPreferredMinAge: Float? = null
	private var newPreferredMaxAge: Float? = null
	private var newRadius: Float? = null
	
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = BtmSheetSettingsPreferencesBinding.inflate(inflater, container, false)
		.apply {
			_binding = this
			executePendingBindings()
		}
		.root


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		initProfile(MainActivity.currentUser!!)
		
		rangeSeekBarAgePicker.addOnChangeListener { rangeSlider, value, fromUser ->
			newPreferredMinAge = rangeSlider.values.first()
			newPreferredMaxAge = rangeSlider.values.last()
			
			tvPickedAgeMin.text = "${rangeSlider.values.first().toInt()}"
			tvPickedAgeMax.text = "${rangeSlider.values.last().toInt()}"
		}

		
		toggleButtonPickerPreferredGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
			if (group.checkedButtonIds.size > 1)
				newPreferredGender = EVERYONE
			
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderMale)
				newPreferredGender = MALE
			
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderFemale)
				newPreferredGender = FEMALE
		}
		
		sliderRadius.addOnChangeListener { slider, value, fromUser ->
			newRadius = value
		}
	}

	private fun initProfile(userItem: UserItem) = binding.run {
		rangeSeekBarAgePicker.setValues(
			userItem.preferences.ageRange.minAge.toFloat(),
			userItem.preferences.ageRange.maxAge.toFloat()
		)
		tvPickedAgeMin.text = "${userItem.preferences.ageRange.minAge}"
		tvPickedAgeMax.text = "${userItem.preferences.ageRange.maxAge}"
		
		when (userItem.preferences.gender) {
			MALE -> {
				toggleButtonPickerPreferredGender.clearChecked()
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderMale)
			}
			FEMALE -> {
				toggleButtonPickerPreferredGender.clearChecked()
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderFemale)
			}
			EVERYONE -> {
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderMale)
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderFemale)
			}
		}
		
		sliderRadius.value = userItem.preferences.radius.toFloat()
		
	}
	
	override fun onStop() {
		if (newPreferredGender != null || newPreferredMaxAge != null || newPreferredMinAge != null || newRadius != null)
			with(MainActivity.currentUser!!.preferences) {
				sharedViewModel.updateUser(
					MainActivity.currentUser!!.copy(
						preferences = copy(
							gender = newPreferredGender ?: gender,
							ageRange = ageRange.copy(
								minAge = newPreferredMinAge?.toInt() ?: ageRange.minAge,
								maxAge = newPreferredMaxAge?.toInt() ?: ageRange.maxAge
							),
							radius = (newRadius ?: radius).toDouble()
						)
					)
				)
			}
			
		super.onStop()
	}
}