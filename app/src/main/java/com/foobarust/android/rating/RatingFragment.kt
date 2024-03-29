package com.foobarust.android.rating

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentRatingBinding
import com.foobarust.android.order.SAVED_STATE_KEY_RATING_COMPLETED
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 2/24/21
 */

@AndroidEntryPoint
class RatingFragment : FullScreenDialogFragment(R.layout.fragment_rating), Toolbar.OnMenuItemClickListener {

    private val binding: FragmentRatingBinding by viewBinding(FragmentRatingBinding::bind)
    private lateinit var navController: NavController
    private lateinit var viewModel: RatingViewModel
    private val navArgs: RatingFragmentArgs by navArgs()

    override var onBackPressed: (() -> Unit)? = { handleBackPressed() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup navigation
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Get the nav graph viewModel instance
        viewModel = getHiltNavGraphViewModel<RatingViewModel>(
            navGraphId = R.id.navigation_rating,
            navController = navController
        ).apply {
            onFetchOrderDetail(navArgs.orderId)
        }

        // Record current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.onUpdateCurrentDestination(destination.id)
        }

        // Setup toolbar
        with(binding.toolbar) {
            setOnMenuItemClickListener(this@RatingFragment)
            setNavigationOnClickListener {
                handleBackPressed()
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchOrderDetail(navArgs.orderId)
        }

        // Set toolbar title
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderDetail.collect { orderDetail ->
                orderDetail?.let {
                    binding.toolbar.title = getString(R.string.rating_toolbar_title, it.identifier)
                }
            }
        }

        // Load ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ratingUiLoadState.collect { uiLoadState ->
                with(binding) {
                    fragmentContainer.isVisible = uiLoadState !is RatingUiState.Error
                    loadingProgressBar.isVisible = uiLoadState is RatingUiState.Loading
                    loadErrorLayout.loadErrorLayout.isVisible = uiLoadState is RatingUiState.Error
                }

                if (uiLoadState is RatingUiState.Error) {
                    showShortToast(uiLoadState.message)
                }
            }
        }

        // Submit ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ratingUiSubmitState.collect { uiState ->
                if (uiState == null) return@collect

                binding.loadingProgressBar.isVisible = uiState is RatingUiState.Loading

                when (uiState) {
                    RatingUiState.Success -> findNavController(R.id.ratingFragment)
                        ?.previousBackStackEntry?.savedStateHandle?.set(
                            SAVED_STATE_KEY_RATING_COMPLETED, true
                        )
                    is RatingUiState.Error -> showShortToast(uiState.message)
                    RatingUiState.Loading -> Unit
                }
            }
        }

        // Complete rating
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ratingCompleted.collect {
                findNavController(R.id.ratingFragment)?.run {
                    navigateUp()
                }
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_order_detail -> findNavController(R.id.ratingFragment)?.navigate(
                RatingFragmentDirections.actionRatingFragmentToOrderDetailFragment(navArgs.orderId)
            )
        }

        return true
    }

    private fun handleBackPressed() {
        // Dismiss the dialog when back pressing in start destination
        when (navController.currentDestination?.id) {
            R.id.ratingOrderFragment, R.id.ratingCompleteFragment -> {
                findNavController(R.id.ratingFragment)?.navigateUp()
            }
            else -> {
                navController.navigateUp()
            }
        }
    }
}