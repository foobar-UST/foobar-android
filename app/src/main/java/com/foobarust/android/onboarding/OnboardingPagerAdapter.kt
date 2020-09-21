package com.foobarust.android.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val onboardingProperties: List<OnboardingProperty>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = onboardingProperties.size

    override fun createFragment(position: Int): Fragment {
        return onboardingProperties[position].let {
            OnboardingFragment.newInstance(onboardingProperties[position])
        }
    }
}

