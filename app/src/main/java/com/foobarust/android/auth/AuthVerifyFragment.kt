package com.foobarust.android.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.auth.SignInState.INPUT
import com.foobarust.android.databinding.FragmentAuthVerifyBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.OnTextViewClickableSpanListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthVerifyFragment : Fragment(), OnTextViewClickableSpanListener {

    private var binding: FragmentAuthVerifyBinding by AutoClearedValue(this)
    private val viewModel: AuthViewModel by activityViewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.onAuthEmailVerifyingCanceled()
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
    ): View? {
        binding = FragmentAuthVerifyBinding.inflate(inflater, container, false).apply {
            viewModel = this@AuthVerifyFragment.viewModel
            listener = this@AuthVerifyFragment
        }

        // Navigate back to input fragment
        viewModel.signInState.observe(viewLifecycleOwner) { state ->
            if (state == INPUT) {
                findNavController().navigate(
                    AuthVerifyFragmentDirections.actionAuthVerifyFragmentToAuthInputFragment()
                )
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        backPressedCallback.remove()
        super.onDestroy()
    }

    override fun onClickableSpanEndClicked(view: View) {
        when (view.id) {
            R.id.no_email_text_view -> viewModel.onRequestAuthEmail()
        }
    }
}