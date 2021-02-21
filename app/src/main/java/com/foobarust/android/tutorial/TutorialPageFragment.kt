package com.foobarust.android.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.foobarust.android.databinding.FragmentTutorialPageBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.parentViewModels
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_TUTORIAL_PAGE = "tutorial_page"

@AndroidEntryPoint
class TutorialPageFragment : Fragment() {

    private var binding: FragmentTutorialPageBinding by AutoClearedValue(this)
    private val viewModel: TutorialViewModel by parentViewModels()
    private val tutorialPage: TutorialPage by lazy {
        requireArguments().getParcelable<TutorialPage>(ARG_TUTORIAL_PAGE) ?:
        throw IllegalArgumentException("Tutorial property not found.")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTutorialPageBinding.inflate(inflater, container, false).apply {
            tutorialPage = this@TutorialPageFragment.tutorialPage
            lifecycleOwner = viewLifecycleOwner
        }

        // Dismiss dialog when completed
        if (tutorialPage.showDismiss) {
            binding.completeButton.setOnClickListener {
                viewModel.onCompleteTutorial()
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance(property: TutorialPage): TutorialPageFragment {
            return TutorialPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TUTORIAL_PAGE, property)
                }
            }
        }
    }
}