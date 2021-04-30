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
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentAuthInputBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
            authViewModel.onSignInSkipped()
        }

        // Restore saved input values
        with(binding) {
            usernameEditText.setText(authViewModel.getSavedUsernameInput())
            emailDomainsTextView.setText(authViewModel.getSavedEmailDomainInput().title, false)
        }

        // Setup email domains list
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.emailDomains.collect { domains ->
                val emailDomainsAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.auth_email_domain_item,
                    domains.map { it.title }
                )

                with(binding.emailDomainsTextView) {
                    setAdapter(emailDomainsAdapter)
                    setOnItemClickListener { _, _, position, _ ->
                        authViewModel.onEmailDomainUpdated(domains[position])
                    }
                }
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authUiState.collect { uiState ->
                with(binding) {
                    loadingProgressBar.isVisible = uiState == AuthUiState.REQUESTING
                    signInButtonsGroup.isVisible = uiState == AuthUiState.INPUT
                }

                if (uiState == AuthUiState.VERIFYING) {
                    findNavController(R.id.authInputFragment)?.navigate(
                        AuthInputFragmentDirections.actionAuthInputFragmentToAuthVerifyFragment()
                    )
                } else if (uiState == AuthUiState.COMPLETED) {
                    findNavController(R.id.authInputFragment)?.navigate(
                        AuthInputFragmentDirections.actionAuthInputFragmentToMainActivity()
                    )
                }
            }
        }

        return binding.root
    }
}