package com.foobarust.android.rating

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentRatingOrderBinding
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.viewBinding
import com.foobarust.domain.models.order.OrderType
import com.foobarust.domain.models.order.getNormalizedSellerName
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 2/25/21
 */

@AndroidEntryPoint
class RatingOrderFragment : Fragment(R.layout.fragment_rating_order) {

    private val binding: FragmentRatingOrderBinding by viewBinding(FragmentRatingOrderBinding::bind)
    private val ratingViewModel: RatingViewModel by hiltNavGraphViewModels(R.id.navigation_rating)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore saved rating
        ratingViewModel.orderRatingInput?.let {
            binding.ratingBar.rating = it.toFloat()
        }

        // For on-campus order, navigate to comment screen.
        // For off-campus order, navigate to delivery screen.
        binding.navigateButton.setOnClickListener {
            val orderType = ratingViewModel.orderDetail.value?.type ?: return@setOnClickListener
            when (orderType) {
                OrderType.ON_CAMPUS -> findNavController(R.id.ratingOrderFragment)?.navigate(
                    RatingOrderFragmentDirections.actionRatingOrderFragmentToRatingCommentFragment()
                )
                OrderType.OFF_CAMPUS -> findNavController(R.id.ratingOrderFragment)?.navigate(
                    RatingOrderFragmentDirections.actionRatingOrderFragmentToRatingDeliveryFragment()
                )
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
                orderDetail?.let {
                    binding.ratingSellerNameTextView.text = it.getNormalizedSellerName()
                    binding.ratingImageView.loadGlideUrl(
                        imageUrl = it.imageUrl,
                        circularCrop = true,
                        placeholder = R.drawable.placeholder_card
                    )
                }
            }
        }
    }
}