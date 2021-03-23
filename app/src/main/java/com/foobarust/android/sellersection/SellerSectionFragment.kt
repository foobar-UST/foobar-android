package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSectionBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.getHiltNavGraphViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerSectionBinding by AutoClearedValue(this)
    private lateinit var navController: NavController
    private lateinit var viewModel: SellerSectionViewModel
    private val navArgs: SellerSectionFragmentArgs by navArgs()

    override var onBackPressed: (() -> Unit)? = { handleBackPressed() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionBinding.inflate(inflater, container, false)

        // Setup navigation
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_container)
            as NavHostFragment
        navController = navHostFragment.navController.apply {
            setGraph(
                R.navigation.navigation_seller_section,
                navArgs.toBundle()
            )
        }

        // Get the nav graph viewModel instance
        viewModel = getHiltNavGraphViewModel(
            navGraphId = R.id.navigation_seller_section,
            navController = navController
        )

        // Record current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.onUpdateCurrentDestination(destination.id)
        }

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        // Toolbar title
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toolbarTitle.collect {
                binding.toolbar.title = it
            }
        }

        // Navigate to seller detail
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerDetail.collect {
                findNavController(R.id.sellerSectionFragment)?.navigate(
                    SellerSectionFragmentDirections.actionSellerSectionFragmentToSellerDetailFragment(
                        property = it
                    )
                )
            }
        }

        // Navigate to seller misc
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerMisc.collect {
                findNavController(R.id.sellerSectionFragment)?.navigate(
                    SellerSectionFragmentDirections.actionSellerSectionFragmentToSellerMiscFragment(
                        sellerId = it
                    )
                )
            }
        }

        // Navigate to another section
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerSection.collect {
                findNavController(R.id.sellerSectionFragment)?.navigate(
                    SellerSectionFragmentDirections.actionSellerSectionFragmentSelf(sectionId = it)
                )
            }
        }

        return binding.root
    }

    private fun handleBackPressed() {
        // Dismiss the dialog when back pressing in start destination
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.sellerSectionDetailFragment) {
            findNavController(R.id.sellerSectionFragment)?.navigateUp()
        } else {
            viewModel.onBackPressed()
        }
    }
}