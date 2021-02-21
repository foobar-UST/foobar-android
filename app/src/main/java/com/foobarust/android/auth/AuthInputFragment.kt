package com.foobarust.android.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentAuthInputBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthInputFragment : Fragment() {

    private var binding: FragmentAuthInputBinding by AutoClearedValue(this)
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthInputBinding.inflate(inflater, container, false)

        // Pass username input to view model
        binding.usernameEditText.doOnTextChanged { text, _, _, _ ->
            authViewModel.onUsernameUpdated(text.toString())
        }

        // Sign-in button
        binding.confirmButton.setOnClickListener {
            authViewModel.onRequestAuthEmail()
        }

        // Skip login button
        binding.skipButton.setOnClickListener {
            authViewModel.onSkipSignIn()
        }

        // Setup email domains drop down menu
        val emailDomainsAdapter = ArrayAdapter(
            requireContext(),
            R.layout.auth_email_domain_item,
            authViewModel.emailDomains.map { it.title }
        )

        binding.emailDomainsTextView.run {
            setAdapter(emailDomainsAdapter)
            setOnItemClickListener { _, _, position, _ ->
                authViewModel.onEmailDomainSelected(authViewModel.emailDomains[position])
            }
            // Select first item
            setText(emailDomainsAdapter.getItem(0), false)
        }

        authViewModel.authUiState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                loadingProgressBar.isVisible = state == AuthUiState.REQUESTING
                signInButtonsGroup.isVisible = state == AuthUiState.INPUT
            }

            when (state) {
                // Proceed to email verify state
                AuthUiState.VERIFYING -> findNavController(R.id.authInputFragment)?.navigate(
                    AuthInputFragmentDirections.actionAuthInputFragmentToAuthVerifyFragment()
                )
                // Skip sign in
                AuthUiState.COMPLETED -> findNavController(R.id.authInputFragment)?.navigate(
                    AuthInputFragmentDirections.actionAuthInputFragmentToMainActivity()
                )
                else -> Unit
            }
        }

        return binding.root
    }
}