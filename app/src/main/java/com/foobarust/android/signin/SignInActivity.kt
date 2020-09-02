package com.foobarust.android.signin

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.foobarust.android.NavigationSigninDirections
import com.foobarust.android.R
import com.foobarust.android.databinding.ActivitySigninBinding
import com.foobarust.android.signin.AuthState.*
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 8/26/20
 */

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    private lateinit var navController: NavController

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)

        // Setup Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.signin_nav_container_view) as NavHostFragment
        navController = navHostFragment.navController

        // Toast
        viewModel.message.observe(this) {
            showShortToast(it)
        }

        viewModel.authState.observe(this) { state ->
            when (state) {
                EMAIL_SENT -> {
                    navController.navigate(
                        SignInInputFragmentDirections.actionSignInInputFragmentToSignInVerifyFragment()
                    )
                }
                EMAIL_VERIFIED, SKIPPED -> {
                    navController.navigate(
                        NavigationSigninDirections.actionGlobalOverviewActivity()
                    )
                    finish()
                }
            }
        }

        // Look for sign-in link when the activity starts
        handleEmailDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // Look for sign-in link when the activity resumes
        handleEmailDeepLink(intent)
    }

    private fun handleEmailDeepLink(intent: Intent?) {
        val emailLink = intent?.data?.toString()
        emailLink?.let {
            viewModel.verifyEmailLinkAndSignIn(it)
        }
    }
}