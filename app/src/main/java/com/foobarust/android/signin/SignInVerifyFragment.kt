package com.foobarust.android.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSigninVerifyBinding
import com.foobarust.android.signin.SignInState.INPUT
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.OnTextViewClickableSpanListener
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 8/26/20
 */

@AndroidEntryPoint
class SignInVerifyFragment : Fragment(), OnTextViewClickableSpanListener {

    private var binding: FragmentSigninVerifyBinding by AutoClearedValue(this)

    private val viewModel: SignInViewModel by activityViewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.onAuthEmailVerifyingCanceled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // When back pressed, cancel the email verification and navigate back to input screen
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninVerifyBinding.inflate(inflater, container, false).apply {
            viewModel = this@SignInVerifyFragment.viewModel
            listener = this@SignInVerifyFragment
        }

        // Navigate back to input fragment
        viewModel.signInState.observe(viewLifecycleOwner) { state ->
            if (state == INPUT) {
                findNavController().navigate(
                    SignInVerifyFragmentDirections.actionSignInVerifyFragmentToSignInInputFragment()
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
            R.id.sigin_verify_no_email_text_view -> viewModel.requestAuthEmail()
            R.id.sigin_verify_tutorial_text_view -> showTutorialBottomSheet()
        }
    }

    private fun showTutorialBottomSheet() {
        findNavController().navigate(
            SignInVerifyFragmentDirections.actionSignInVerifyFragmentToSignInTutorialBottomSheet()
        )
    }
}