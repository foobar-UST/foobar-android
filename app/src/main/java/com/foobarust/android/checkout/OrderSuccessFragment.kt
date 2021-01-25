package com.foobarust.android.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentOrderSuccessBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 1/9/21
 */

@AndroidEntryPoint
class OrderSuccessFragment : Fragment() {

    private var binding: FragmentOrderSuccessBinding by AutoClearedValue(this)
    private val checkoutViewModel: CheckoutViewModel by navGraphViewModels(R.id.navigation_checkout)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderSuccessBinding.inflate(inflater, container, false)

        // Set toolbar title
        checkoutViewModel.onUpdateToolbarTitle(title = "Completed")

        // Hide submit button
        checkoutViewModel.onShowSubmitButton(isShow = false)

        // Observe dialog back press and navigate up
        checkoutViewModel.backPressed.observe(viewLifecycleOwner) {
            // if is success, dismiss the dialog
            // else navigate up
            findNavController().navigateUp()
        }


        return binding.root
    }
}