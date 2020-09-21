package com.foobarust.android.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.foobarust.android.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe for navigation
        viewModel.startNavigation.observe(this) {
            navigateTo(destination = it, fadeAnim = true, finishEnd = true)
        }
    }
}