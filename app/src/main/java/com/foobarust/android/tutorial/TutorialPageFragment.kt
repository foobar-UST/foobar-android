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

private const val TUTORIAL_PROPERTY = "tutorial_property"

@AndroidEntryPoint
class TutorialPageFragment : Fragment() {

    private var binding: FragmentTutorialPageBinding by AutoClearedValue(this)
    private val viewModel: TutorialViewModel by parentViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val property = requireArguments().getParcelable<TutorialProperty>(TUTORIAL_PROPERTY) ?:
            throw IllegalArgumentException("Tutorial property not found.")

        binding = FragmentTutorialPageBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            onboardingProperty = property
        }

        // Dismiss dialog when completed
        if (property.showCompleteButton) {
            binding.completeButton.setOnClickListener {
                viewModel.onTutorialCompleted()
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance(property: TutorialProperty): TutorialPageFragment {
            return TutorialPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TUTORIAL_PROPERTY, property)
                }
            }
        }
    }
}