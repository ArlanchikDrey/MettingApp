

package com.dev.meeting.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.dev.meeting.R
import com.dev.meeting.databinding.FragmentAuthLandingBinding
import com.dev.meeting.ui.common.base.BaseFragment
import com.dev.meeting.utils.extensions.showToastText
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthLandingFragment: BaseFragment<AuthViewModel, FragmentAuthLandingBinding>(
	layoutId = R.layout.fragment_auth_landing
) {
	
	override val mViewModel: AuthViewModel by viewModels()
	
	//Progress dialog for any authentication action
//	private lateinit var mCallbackManager: CallbackManager
	private lateinit var googleSignInClient: GoogleSignInClient

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

//		mCallbackManager = CallbackManager.Factory.create()
		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(requireActivity().getString(R.string.default_web_client_id))
			.requestEmail()
			.build()
		googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
//		btnFacebookLogin.fragment = this@AuthLandingFragment
//		btnFacebookLogin.registerCallback(mCallbackManager, object: FacebookCallback<LoginResult> {
//
//			override fun onSuccess(loginResult: LoginResult) {
//				mViewModel.signIn(loginResult.accessToken.token)
//			}
//
//			override fun onCancel() {}
//
//			override fun onError(error: FacebookException) {
//				view.context.showToastText("$error")
//			}
//		})
		btnFacebookLoginDelegate.setOnClickListener {
			sharedViewModel.logOut()
			startActivityForResult(googleSignInClient.signInIntent, Ints.RC_SIGN_IN)
		}
//
//		tvOpenPolicies.setOnClickListener {
//			var url = getString(R.string.privacy_policy_url)
//			if (!url.startsWith("http://") && !url.startsWith("https://"))
//				url = "http://$url"
//			val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//			startActivity(browserIntent)
//		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
//		mCallbackManager.onActivityResult(requestCode, resultCode, data)
		if (requestCode == Ints.RC_SIGN_IN) {
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			try {
				// Google Sign In was successful, authenticate with Firebase
				val account = task.getResult(ApiException::class.java)
				if(account != null)
					mViewModel.signIn(account)
			} catch (e: ApiException) {
				Log.w(TAG, "Google sign in failed", e)
			}
		}
	}

}

object Ints {
	const val RC_SIGN_IN = 10653
}

