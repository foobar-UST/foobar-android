package com.foobarust.android.tutorial

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentTutorialPageBinding
import com.foobarust.android.utils.parentViewModels
import com.foobarust.android.utils.setSrc
import com.foobarust.android.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_TUTORIAL_PAGE = "tutorial_page"

@AndroidEntryPoint
class TutorialPageFragment : Fragment(R.layout.fragment_tutorial_page) {

    private val binding: FragmentTutorialPageBinding by viewBinding(FragmentTutorialPageBinding::bind)
    private val viewModel: TutorialViewModel by parentViewModels()

    private val tutorialPage: TutorialPage by lazy {
        requireArguments().getParcelable<TutorialPage>(ARG_TUTORIAL_PAGE) ?:
            throw IllegalArgumentException("Tutorial property not found.")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            iconImageView.setSrc(tutorialPage.drawableRes)
            titleTextView.text = tutorialPage.title
            descriptionTextView.text = tutorialPage.description
            completeButton.isInvisible = !tutorialPage.showDismiss
        }

        // Dismiss dialog when completed
        if (tutorialPage.showDismiss) {
            binding.completeButton.setOnClickListener {
                viewModel.onCompleteTutorial()
            }
        }
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