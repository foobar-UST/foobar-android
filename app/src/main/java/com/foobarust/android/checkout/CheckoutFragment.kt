package com.foobarust.android.checkout

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentCheckoutBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.getNavGraphViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 1/9/21
 */
@AndroidEntryPoint
class CheckoutFragment : FullScreenDialogFragment() {

    private var binding: FragmentCheckoutBinding by AutoClearedValue(this)
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        setupNavigation()

        // Setup binding
        binding.run {
            viewModel = this@CheckoutFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup toolbar navigation button
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        // Expose submit button click event to child fragments
        binding.submitButton.setOnClickListener {
            viewModel.onSubmitButtonClicked()
        }

        // Navigate to SellerDetail
        viewModel.navigateToSellerDetail.observe(viewLifecycleOwner) {
            findNavController(R.id.checkoutFragment)?.navigate(
                CheckoutFragmentDirections.actionCheckoutFragmentToSellerDetailFragment(property = it)
            )
        }

        // Navigate to SellerMisc
        viewModel.navigateToSellerMisc.observe(viewLifecycleOwner) {
            findNavController(R.id.checkoutFragment)?.navigate(
                CheckoutFragmentDirections.actionCheckoutFragmentToSellerMiscFragment(
                    sellerId = it
                )
            )
        }

        // Navigate to SellerItemDetail
        viewModel.navigateToSellerItemDetail.observe(viewLifecycleOwner) {
            findNavController(R.id.checkoutFragment)?.navigate(
                CheckoutFragmentDirections.actionCheckoutFragmentToSellerItemDetailFragment(
                    property = it
                )
            )
        }

        // Navigate to SellerSection
        viewModel.navigateToSellerSection.observe(viewLifecycleOwner) {
            findNavController(R.id.checkoutFragment)?.navigate(
                CheckoutFragmentDirections.actionCheckoutFragmentToSellerSectionFragment(
                    property = it
                )
            )
        }

        // Expand collapsing toolbar
        viewModel.expandCollapsingToolbar.observe(viewLifecycleOwner) {
            binding.appBarLayout.setExpanded(true, true)
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
        navController.setGraph(R.navigation.navigation_checkout)

        // Scope CheckoutViewModel to navigation graph
        viewModel = getNavGraphViewModel(
            navGraphId = R.id.navigation_checkout,
            navController = navController
        )

        // Record current destination
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            // Keep track of current fragment destination
            viewModel.onUpdateCurrentDestination(destination.id)
        }
    }

    private fun handleBackPressed() {
        // Dismiss the dialog when back pressing in start destination
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.cartFragment) {
            findNavController().navigateUp()
        } else {
            viewModel.onBackPressed()
        }
    }
}