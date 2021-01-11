package com.foobarust.android.sellersection

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentSellerSectionBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.getNavGraphViewModel
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerSectionBinding by AutoClearedValue(this)
    private lateinit var viewModel: SellerSectionViewModel
    private lateinit var navController: NavController
    private val navArgs: SellerSectionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionBinding.inflate(inflater, container, false)

        setupNavigation()

        binding.run {
            viewModel = this@SellerSectionFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.onFetchSectionDetail(property = navArgs.sellerSectionProperty)

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        // Navigate to seller detail
        viewModel.navigateToSellerDetail.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerSectionFragment)?.navigate(
                SellerSectionFragmentDirections.actionSellerSectionFragmentToSellerDetailFragment(
                    sellerId = it
                )
            )
        }

        // Navigate to seller misc
        viewModel.navigateToSellerMisc.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerSectionFragment)?.navigate(
                SellerSectionFragmentDirections.actionSellerSectionFragmentToSellerMiscFragment(
                    sellerId = it
                )
            )
        }

        // Navigate to another section
        viewModel.navigateToSellerSection.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerSectionFragment)?.navigate(
                SellerSectionFragmentDirections.actionSellerSectionFragmentSelf(
                    sellerSectionProperty = it
                )
            )
        }

        // Show toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchSectionDetail(property = navArgs.sellerSectionProperty)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                handleBackPressed()
            }
        }
    }

    private fun setupNavigation() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.navigation_seller_section)

        // Cannot initialize ViewModel using navGraphViewModels() as findNavController() is broken at the moment
        viewModel = getNavGraphViewModel(
            navGraphId = R.id.navigation_seller_section,
            navController = navController
        )

        // Record current destination
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.onUpdateCurrentDestination(destination.id)
        }
    }

    private fun handleBackPressed() {
        // Dismiss the dialog when back pressing in start destination
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.sellerSectionDetailFragment) {
            dismiss()
        } else {
            viewModel.onBackPressed()
        }
    }
}