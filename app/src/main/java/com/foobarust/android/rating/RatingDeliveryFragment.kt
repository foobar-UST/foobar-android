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
            navigateToRatingComment()
        }

        // Thumb down button
        binding.thumbDownButton.setOnClickListener {
            ratingViewModel.onUpdateDeliveryRating(false)
            navigateToRatingComment()
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

        return binding.root
    }

    private fun navigateToRatingComment() {
        findNavController(R.id.ratingDeliveryFragment)?.navigate(
            RatingDeliveryFragmentDirections.actionRatingDeliveryFragmentToRatingCommentFragment()
        )
    }
}