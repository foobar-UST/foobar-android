package com.foobarust.android.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import com.foobarust.android.databinding.FragmentTutorialBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
            tutorialPages = tutorialViewModel.tutorialPageProperties
        )

        binding.viewPager.run {
            adapter = pagerAdapter
            getChildAt(0).overScrollMode = OVER_SCROLL_NEVER
        }

        // Setup TabLayout
        TabLayoutMediator(binding.indicatorTabLayout, binding.viewPager) { _, _ -> }.attach()

        // Dismiss tutorial
        viewLifecycleOwner.lifecycleScope.launch {
            tutorialViewModel.dismissTutorial.collect {
                dismiss()
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "TutorialFragment"

        @JvmStatic
        fun newInstance() : TutorialFragment = TutorialFragment()
    }
}