package com.foobarust.android.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentTutorialBinding
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialFragment : FullScreenDialogFragment() {

    private var binding: FragmentTutorialBinding by AutoClearedValue(this)
    private val tutorialViewModel: TutorialViewModel by viewModels()

    override var onBackPressed: (() -> Unit)? = {
        tutorialViewModel.onCompleteTutorial()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialBinding.inflate(inflater, container, false)

        // Setup ViewPager
        val pagerAdapter = TutorialPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle,
            tutorialProperties = tutorialViewModel.tutorialProperties
        )

        binding.viewPager.run {
            adapter = pagerAdapter
            getChildAt(0).overScrollMode = OVER_SCROLL_NEVER
        }

        // Setup TabLayout
        TabLayoutMediator(binding.indicatorTabLayout, binding.viewPager) { _, _ -> }.attach()

        // Dismiss tutorial
        tutorialViewModel.dismissTutorial.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }

    companion object {
        const val TAG = "TutorialFragment"
    }
}