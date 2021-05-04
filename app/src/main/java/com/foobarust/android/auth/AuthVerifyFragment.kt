package com.foobarust.android.auth

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentAuthVerifyBinding
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.themeColor
import com.foobarust.android.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthVerifyFragment : Fragment(R.layout.fragment_auth_verify) {

    private val binding: FragmentAuthVerifyBinding by viewBinding(FragmentAuthVerifyBinding::bind)
    private val authViewModel: AuthViewModel by activityViewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            authViewModel.onEmailVerificationCancelled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // When back pressed, cancel the email verification and
        // navigate back to input screen
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authUiState.collect { uiState ->
                when (uiState) {
                    AuthUiState.INPUT -> {
                        findNavController(R.id.authVerifyFragment)?.navigate(
                            AuthVerifyFragmentDirections.actionAuthVerifyFragmentToAuthInputFragment()
                        )
                    }
                    AuthUiState.COMPLETED -> {
                        findNavController(R.id.authVerifyFragment)?.navigate(
                            AuthVerifyFragmentDirections.actionAuthVerifyFragmentToMainActivity()
                        )
                        requireActivity().finish()
                    }
                    else -> Unit
                }
            }
        }

        setupResendEmailClickSpan(binding.resendEmailTextView)
    }

    override fun onDestroy() {
        backPressedCallback.remove()
        super.onDestroy()
    }

    private fun setupResendEmailClickSpan(resendTextView: TextView) {
        val builder = SpannableStringBuilder(resendTextView.text)
        val spanString = getString(R.string.auth_verify_resend_email)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                authViewModel.onRequestAuthEmail()
                view.invalidate()
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = requireContext().themeColor(R.attr.colorPrimary)
                textPaint.isFakeBoldText = true
            }
        }

        // Insert span at the end
        builder.run {
            append(spanString)
            setSpan(
                clickableSpan,
                builder.length - spanString.length,
                builder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        resendTextView.run {
            movementMethod = LinkMovementMethod.getInstance()
            setText(builder, TextView.BufferType.SPANNABLE)
        }
    }
}