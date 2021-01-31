package com.foobarust.android.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.foobarust.android.R
import com.foobarust.android.databinding.ActivityAuthBinding
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityAuthBinding>(
            this,
            R.layout.activity_auth
        ).apply {
            viewModel = this@AuthActivity.viewModel
        }

        // Setup Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Show toast
        viewModel.toastMessage.observe(this) {
            showShortToast(it)
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
        if (emailLink != null) {
            viewModel.onVerifyEmailLinkAndSignIn(emailLink)
        }
    }
}