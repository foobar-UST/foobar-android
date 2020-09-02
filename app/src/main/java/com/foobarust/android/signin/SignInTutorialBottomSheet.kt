package com.foobarust.android.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foobarust.android.databinding.BottomSheetSigninTutorialBinding
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by kevin on 9/3/20
 */

class SignInTutorialBottomSheet : BottomSheetDialogFragment() {

    private var binding: BottomSheetSigninTutorialBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetSigninTutorialBinding.inflate(inflater, container, false)

        return binding.root
    }
}