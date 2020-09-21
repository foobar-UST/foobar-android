package com.foobarust.android.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.foobarust.android.databinding.FragmentOnboardingBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

private const val ONBOARDING_PROPERTY = "onboarding_property"

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var binding: FragmentOnboardingBinding by AutoClearedValue(this)

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val property = requireArguments().getParcelable<OnboardingProperty>(ONBOARDING_PROPERTY)!!

        binding = FragmentOnboardingBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            onboardingProperty = property
        }

        if (property.showCompleteButton) {
            binding.completeButton.setOnClickListener {
                viewModel.onboardingCompleted()
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance(onboardingProperty: OnboardingProperty): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ONBOARDING_PROPERTY, onboardingProperty)
                }
            }
        }
    }
}