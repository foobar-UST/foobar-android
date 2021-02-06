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
            this, R.layout.activity_auth
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

        // Finish activity if the user is already signed in.
        viewModel.userSignedIn.observe(this) {
            finish()
        }

        // Look for sign-in link when the activity starts
        handleEmailDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // onNewIntent() will be called when AuthActivity is
        // launched the second time with singleTop launch mode being set,
        // instead of creating another activity instance.
        handleEmailDeepLink(intent)
    }

    private fun handleEmailDeepLink(intent: Intent?) {
        val emailLink = intent?.data?.toString()
        if (emailLink != null) {
            viewModel.onSignInWithEmailLink(emailLink)
        }
    }
}