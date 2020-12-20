package com.foobarust.android.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.auth.SignInState.VERIFYING
import com.foobarust.android.databinding.FragmentAuthInputBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthInputFragment : Fragment() {

    private var binding: FragmentAuthInputBinding by AutoClearedValue(this)
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthInputBinding.inflate(inflater, container, false)

        // Pass username input to view model
        binding.usernameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameUpdated(text.toString())
        }

        // Sign-in button
        binding.confirmButton.setOnClickListener {
            viewModel.onRequestAuthEmail()
        }

        // Skip login button
        binding.skipSigninButton.setOnClickListener {
            viewModel.onSignInSkip()
        }

        // Setup email domains drop down
        viewModel.emailDomains.observe(viewLifecycleOwner) { domains ->
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.auth_email_domain_item,
                domains.map { it.title }
            )

            binding.domainsAutoCompleteTextView.run {
                setAdapter(adapter)
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.onAuthEmailDomainUpdated(domains[position])
                }

                // Select first item
                setText(adapter.getItem(0), false)
                //viewModel.onAuthEmailDomainChanged(domains[0])
            }
        }

        // Navigate to VerifyFragment after successfully requesting an email
        viewModel.signInState.observe(viewLifecycleOwner) { state ->
            if (state == VERIFYING) {
                findNavController().navigate(
                    AuthInputFragmentDirections.actionAuthInputFragmentToAuthVerifyFragment()
                )
            }
        }

        return binding.root
    }
}