
package com.dev.meeting.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dev.meeting.R
import com.dev.meeting.databinding.FragmentMainFlowBinding
import com.dev.meeting.ui.common.base.FlowFragment

/**
 * Main flow fragment, user will access it when auth was successful
 */

class MainFlowFragment: FlowFragment<Nothing, FragmentMainFlowBinding>(
	layoutId = R.layout.fragment_main_flow
) {
	
	override val mViewModel: Nothing? = null

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		//set selected bottom menu item on startup
		binding.bottomNavigationView.selectedItemId = R.id.cardsFragment

		val navHost = childFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
		navController = navHost.findNavController()

		navController.addOnDestinationChangedListener { _, destination, _ ->
			if (destination.id in arrayOf(
						R.id.chatFragment,
						R.id.profileFragment,
						R.id.settingsEditInfoFragment
					)
			) {

				binding.bottomNavigationView.visibility = View.GONE
			}
			else binding.bottomNavigationView.visibility = View.VISIBLE
		}
		
		setupBottomNavigation()
	}
	
	private fun setupBottomNavigation() = binding.bottomNavigationView.run {
		setupWithNavController(navController)
		setOnNavigationItemReselectedListener {
			// do nothing here, it will prevent recreating same fragment
		}
	}


}