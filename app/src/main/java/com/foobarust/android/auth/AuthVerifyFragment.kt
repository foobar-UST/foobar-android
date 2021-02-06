package com.foobarust.android.auth

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentAuthVerifyBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthVerifyFragment : Fragment() {

    private var binding: FragmentAuthVerifyBinding by AutoClearedValue(this)
    private val authViewModel: AuthViewModel by activityViewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            authViewModel.onEmailVerificationCanceled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // When back pressed, cancel the email verification and
        // navigate back to input screen
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthVerifyBinding.inflate(inflater, container, false).apply {
            viewModel = this@AuthVerifyFragment.authViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        authViewModel.authPage.observe(viewLifecycleOwner) { state ->
            if (state == AuthPage.INPUT) {
                findNavController(R.id.authVerifyFragment)?.navigate(
                    AuthVerifyFragmentDirections.actionAuthVerifyFragmentToAuthInputFragment()
                )
            } else if (state == AuthPage.COMPLETED) {
                findNavController(R.id.authVerifyFragment)?.navigate(
                    AuthVerifyFragmentDirections.actionAuthVerifyFragmentToMainActivity()
                )
                requireActivity().finish()
            }
        }

        setupResendEmailClickSpan(binding.resendEmailTextView)

        return binding.root
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