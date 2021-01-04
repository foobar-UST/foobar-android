package com.foobarust.android.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentOrderHistoryBinding
import com.foobarust.android.utils.AutoClearedValue

/**
 * Created by kevin on 9/20/20
 */

class OrderHistoryFragment : FullScreenDialogFragment() {

    private var binding: FragmentOrderHistoryBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }
}