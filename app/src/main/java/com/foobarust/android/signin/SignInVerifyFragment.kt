package com.foobarust.android.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.foobarust.android.databinding.FragmentSigninVerifyBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 8/26/20
 */

@AndroidEntryPoint
class SignInVerifyFragment : Fragment() {

    private var binding: FragmentSigninVerifyBinding by AutoClearedValue(this)

    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninVerifyBinding.inflate(inflater, container, false).apply {
            viewModel = this@SignInVerifyFragment.viewModel
        }

        return binding.root
    }
}