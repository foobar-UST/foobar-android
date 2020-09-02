package com.foobarust.android.overview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.foobarust.android.R
import com.foobarust.android.databinding.ActivityOverviewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_overview)

        binding.overviewTestTextView.text = Firebase.auth.currentUser?.let {
            "Signed In: ${it.email}"
        } ?: "Not signed in."
    }
}