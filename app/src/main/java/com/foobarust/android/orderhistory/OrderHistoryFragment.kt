package com.foobarust.android.orderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.foobarust.android.databinding.FragmentOrderHistoryBinding
import com.foobarust.android.utils.AutoClearedValue

/**
 * Created by kevin on 9/20/20
 */

class OrderHistoryFragment : Fragment() {

    private var binding: FragmentOrderHistoryBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }
}