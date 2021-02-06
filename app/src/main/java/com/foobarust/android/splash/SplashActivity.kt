package com.foobarust.android.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.foobarust.android.R
import com.foobarust.android.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SplashActivity"

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onDispatchDynamicLinks(link = intent.data)

        viewModel.navigateToMain.observe(this) { deepLink ->
            startNavigateToMain(deepLink)
        }
    }

    private fun startNavigateToMain(deepLink: Uri?) {
        val intent = Intent(this, MainActivity::class.java)

        if (deepLink != null) {
            intent.run {
                action = Intent.ACTION_VIEW
                data = deepLink

            }
        }

        startActivity(intent)
        overridePendingTransition(
            R.anim.nav_default_enter_anim,
            R.anim.nav_default_exit_anim
        )

        finish()
    }
}