package com.foobarust.android.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.foobarust.android.R
import com.foobarust.android.databinding.ActivityAuthBinding
import com.foobarust.android.utils.setLayoutFullscreen
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutFullscreen()

        binding = ActivityAuthBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        // Setup Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Show toast
        lifecycleScope.launchWhenStarted {
            viewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        // Finish activity if the user is already signed in.
        lifecycleScope.launchWhenStarted {
            viewModel.isUserSignedIn.collect {
                finish()
            }
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