package com.foobarust.android.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.foobarust.android.R
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KClass

/**
 * Created by kevin on 8/26/20
 */

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe navigation
        viewModel.startNavigation.observe(this) {
            navigateToNextActivity(it)
        }
    }

    private fun navigateToNextActivity(k: KClass<*>) {
        val intent = Intent(this, k.java)
        startActivity(intent)

        overridePendingTransition(
            R.anim.nav_default_enter_anim,
            R.anim.nav_default_exit_anim
        )

        finish()
    }
}