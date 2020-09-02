package com.foobarust.android.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSigninInputBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 8/26/20
 */

@AndroidEntryPoint
class SignInInputFragment : Fragment() {

    private var binding: FragmentSigninInputBinding by AutoClearedValue(this)

    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninInputBinding.inflate(inflater, container, false)

        // Pass username input to view model
        binding.signinInputUsernameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameChanged(text.toString())
        }

        // Sign-in button
        binding.signinInputConfirmButton.setOnClickListener {
            viewModel.requestAuthEmail()
        }

        // Skip login button
        binding.signinInputSkipSigninButton.setOnClickListener {
            viewModel.skipSignIn()
        }

        // Setup email domains drop down
        viewModel.emailDomains.observe(viewLifecycleOwner) { domains ->
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_email_suffix,
                domains.map { it.title }
            )

            binding.signinInputDomainsAutoCompleteTextView.run {
                setAdapter(adapter)
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.onAuthEmailDomainChanged(domains[position])
                }

                // Select first item
                setText(adapter.getItem(0), false)
                //viewModel.onAuthEmailDomainChanged(domains[0])
            }
        }

        return binding.root
    }

    /*
    private fun setupEmailSuffixDropDown() {
        val emailSuffixAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_email_suffix,
            AuthEmailType.values().map { it.title }
        )

        binding.signinInputEmailSuffixDropdown.run {
            setAdapter(emailSuffixAdapter)
            setOnItemClickListener { _, _, position, _ ->
                viewModel.updateAuthEmailType(AuthEmailType.values()[position])
            }
            setText(adapter.getItem(0) as String, false)
        }
    }

     */
}