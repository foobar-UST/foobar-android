package com.foobarust.android.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentTutorialBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.getColorCompat
import com.foobarust.android.utils.themeColor
import com.zhpan.indicator.enums.IndicatorSlideMode
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

        binding.tutorialViewPager.run {
            adapter = pagerAdapter
            getChildAt(0).overScrollMode = OVER_SCROLL_NEVER
        }

        // Setup TabLayout
        binding.scrollIndicator.run {
            setSliderColor(
                normalColor = requireContext().getColorCompat(R.color.material_on_surface_stroke),
                selectedColor = requireContext().themeColor(R.attr.colorSecondary)
            )
            setSlideMode(IndicatorSlideMode.WORM)
            setupWithViewPager(binding.tutorialViewPager)
        }

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