package com.foobarust.android.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentRatingOrderBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.domain.models.order.getNormalizedSellerName
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 2/25/21
 */

@AndroidEntryPoint
class RatingOrderFragment : Fragment() {

    private var binding: FragmentRatingOrderBinding by AutoClearedValue(this)
    private val ratingViewModel: RatingViewModel by hiltNavGraphViewModels(R.id.navigation_rating)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRatingOrderBinding.inflate(inflater, container, false)

        // Restore saved rating
        ratingViewModel.getOrderRating()?.let {
            binding.ratingBar.rating = it.toFloat()
        }

        // Submit rating if it is an on-campus order,
        // else navigate to delivery rating page
        binding.navigateButton.setOnClickListener {
            if (ratingViewModel.shouldRateDelivery()) {
                findNavController(R.id.ratingOrderFragment)?.navigate(
                    RatingOrderFragmentDirections
                        .actionRatingOrderFragmentToRatingDeliveryFragment()
                )
            } else {
                ratingViewModel.onSubmitRating()
            }
        }

        // Store rating
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                ratingViewModel.onUpdateOrderRating(rating.toInt())
            }
        }

        // Order detail
        viewLifecycleOwner.lifecycleScope.launch {
            ratingViewModel.orderDetail.collect { orderDetail ->
                binding.ratingSellerNameTextView.text = orderDetail.getNormalizedSellerName()
                binding.ratingImageView.loadGlideUrl(
                    imageUrl = orderDetail.imageUrl,
                    centerCrop = true,
                    placeholder = R.drawable.placeholder_card
                )
            }
        }

        // Navigate to complete screen if rating is successfully submitted
        viewLifecycleOwner.lifecycleScope.launch {
            ratingViewModel.ratingUiSubmitState.collect { uiState ->
                binding.navigateButton.isGone = uiState is RatingUiState.Loading

                if (uiState is RatingUiState.Success) {
                    findNavController(R.id.ratingOrderFragment)?.navigate(
                        RatingOrderFragmentDirections
                            .actionRatingOrderFragmentToRatingCompleteFragment()
                    )
                }
            }
        }

        return binding.root
    }
}