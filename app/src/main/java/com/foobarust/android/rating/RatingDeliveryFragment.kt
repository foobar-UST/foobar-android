package com.foobarust.android.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentRatingDeliveryBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.bindGlideUrl
import com.foobarust.android.utils.findNavController
import com.foobarust.domain.models.order.getNormalizedSellerName
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 2/25/21
 */

@AndroidEntryPoint
class RatingDeliveryFragment : Fragment() {

    private var binding: FragmentRatingDeliveryBinding by AutoClearedValue(this)
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
        binding = FragmentRatingDeliveryBinding.inflate(inflater, container, false)

        // Thumb up button
        binding.thumbUpButton.setOnClickListener {
            ratingViewModel.onUpdateDeliveryRating(true)
            ratingViewModel.onSubmitRating()
        }

        // Thumb down button
        binding.thumbDownButton.setOnClickListener {
            ratingViewModel.onUpdateDeliveryRating(false)
            ratingViewModel.onSubmitRating()
        }

        // Order detail
        viewLifecycleOwner.lifecycleScope.launch {
            ratingViewModel.orderDetail.collect { orderDetail ->
                binding.ratingSellerNameTextView.text = orderDetail.getNormalizedSellerName()
                binding.ratingImageView.bindGlideUrl(
                    imageUrl = orderDetail.imageUrl,
                    centerCrop = true
                )
            }
        }

        // Navigate to complete screen if rating is successfully submitted
        viewLifecycleOwner.lifecycleScope.launch {
            ratingViewModel.ratingUiSubmitState.collect {
                if (it is RatingUiState.Success) {
                    findNavController(R.id.ratingDeliveryFragment)?.navigate(
                        RatingDeliveryFragmentDirections
                            .actionRatingDeliveryFragmentToRatingCompleteFragment()
                    )
                }
            }
        }

        return binding.root
    }
}