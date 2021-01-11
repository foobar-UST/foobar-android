package com.foobarust.android.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.foobarust.android.databinding.BottomSheetDeliveryOptionsBinding
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 1/10/21
 */

@AndroidEntryPoint
class DeliveryOptionsBottomSheet : BottomSheetDialogFragment(), DeliveryOptionsAdapter.DeliveryOptionsAdapterListener {

    private var binding: BottomSheetDeliveryOptionsBinding by AutoClearedValue(this)
    private val viewModel: DeliveryOptionsViewModel by viewModels()
    private val navArgs: DeliveryOptionsBottomSheetArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onBuildOptionsListModels(navArgs.deliveryOptionProperties)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDeliveryOptionsBinding.inflate(inflater, container, false)

        val deliveryOptionsAdapter = DeliveryOptionsAdapter()

        binding.recyclerView.run {
            adapter = deliveryOptionsAdapter
            setHasFixedSize(true)
        }

        viewModel.optionsListModels.observe(viewLifecycleOwner) {
            Log.d("DeliveryOptions", "$it")
            deliveryOptionsAdapter.submitList(it)
        }

        return binding.root
    }

    override fun onDeliveryOptionSelected(deliveryOptionId: String) {

    }
}