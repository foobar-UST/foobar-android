package com.foobarust.android.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentCheckoutBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.getHiltNavGraphViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/9/21
 */
@AndroidEntryPoint
class CheckoutFragment : FullScreenDialogFragment() {

    private var binding: FragmentCheckoutBinding by AutoClearedValue(this)
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var navController: NavController

    override var onBackPressed: (() -> Unit)? = { handleBackPressed() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController.apply {
            setGraph(R.navigation.navigation_checkout)
        }

        // Scope CheckoutViewModel to navigation graph
        getHiltNavGraphViewModel<CheckoutViewModel>(
            navGraphId = R.id.navigation_checkout,
            navController = navController
        ).also {
            this.viewModel = it
            binding.viewModel = it
        }

        // Record current destination
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.onUpdateCurrentDestination(destination.id)
        }

        // Setup toolbar navigation button
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        // Expose submit button click event to child fragments
        binding.submitButton.setOnClickListener {
            viewModel.onSubmitButtonClicked()
        }

        // Expand collapsing toolbar
        viewModel.expandCollapsingToolbar.observe(viewLifecycleOwner) {
            binding.appBarLayout.setExpanded(true, true)
        }

        // Navigate to SellerDetail
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerDetail.collect {
                findNavController(R.id.checkoutFragment)?.navigate(
                    CheckoutFragmentDirections.actionCheckoutFragmentToSellerDetailFragment(property = it)
                )
            }
        }

        // Navigate to SellerMisc
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerMisc.collect {
                findNavController(R.id.checkoutFragment)?.navigate(
                    CheckoutFragmentDirections.actionCheckoutFragmentToSellerMiscFragment(
                        sellerId = it
                    )
                )
            }
        }

        // Navigate to SellerItemDetail
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerItemDetail.collect {
                findNavController(R.id.checkoutFragment)?.navigate(
                    CheckoutFragmentDirections.actionCheckoutFragmentToSellerItemDetailFragment(
                        property = it
                    )
                )
            }
        }

        // Navigate to SellerSection
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerSection.collect {
                findNavController(R.id.checkoutFragment)?.navigate(
                    CheckoutFragmentDirections.actionCheckoutFragmentToSellerSectionFragment(it)
                )
            }
        }

        // Dismiss dialog (when the order is placed and return using back pressed)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dismissCheckoutDialog.collect {
                dismiss()
            }
        }

        return binding.root
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