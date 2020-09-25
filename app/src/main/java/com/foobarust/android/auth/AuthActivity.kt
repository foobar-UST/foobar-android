package com.foobarust.android.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.foobarust.android.R
import com.foobarust.android.auth.SignInState.COMPLETED
import com.foobarust.android.databinding.ActivityAuthBinding
import com.foobarust.android.main.MainActivity
import com.foobarust.android.utils.navigateTo
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)

        // Setup Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Toast
        viewModel.toastMessage.observe(this) {
            showShortToast(it)
        }

        // When the user is verified or want to skip the sign-in screen,
        // navigate to MainActivity
        viewModel.signInState.observe(this) { state ->
            if (state == COMPLETED) {
                navigateTo(destination = MainActivity::class, finishEnd = true)
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
            // TODO: navigate to verify
        }
    }
}