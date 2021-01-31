package com.foobarust.android.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentOrderDetailBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 1/28/21
 */

@AndroidEntryPoint
class OrderDetailFragment : FullScreenDialogFragment() {

    private var binding: FragmentOrderDetailBinding by AutoClearedValue(this)
    private val viewModel: OrderDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

}