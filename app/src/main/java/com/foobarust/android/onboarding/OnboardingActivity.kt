package com.foobarust.android.onboarding

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import com.foobarust.android.R
import com.foobarust.android.databinding.ActivityOnboardingBinding
import com.foobarust.android.utils.navigateTo
import com.foobarust.android.utils.showShortToast
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)

        // Setup ViewPager
        val pagerAdapter = OnboardingPagerAdapter(this, viewModel.onboardingProperties)
        binding.viewPager.run {
            adapter = pagerAdapter
            getChildAt(0).overScrollMode = OVER_SCROLL_NEVER
        }

        // Setup TabLayout
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        // Navigate to OverviewActivity
        viewModel.navigateToMain.observe(this) {
            navigateTo(destination = it, finishEnd = true)
        }

        // Toast
        viewModel.toastMessage.observe(this) {
            showShortToast(it)
        }
    }
}