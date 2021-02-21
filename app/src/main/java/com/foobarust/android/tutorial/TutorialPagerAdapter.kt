package com.foobarust.android.tutorial

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TutorialPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val tutorialPages: List<TutorialPage>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = tutorialPages.size

    override fun createFragment(position: Int): Fragment {
        return TutorialPageFragment.newInstance(tutorialPages[position])
    }
}

