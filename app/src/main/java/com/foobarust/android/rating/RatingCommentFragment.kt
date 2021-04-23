package com.foobarust.android.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentRatingCommentBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.domain.models.order.getNormalizedSellerName
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 4/24/21
 */

@AndroidEntryPoint
class RatingCommentFragment : Fragment() {

    private var binding: FragmentRatingCommentBinding by AutoClearedValue(this)
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
        binding = FragmentRatingCommentBinding.inflate(inflater, container,false)

        // Restore comment
        binding.ratingCommentEditText.setText(ratingViewModel.commentInput)

        // Comment input
        binding.ratingCommentEditText.doOnTextChanged { text, _, _, _ ->
            ratingViewModel.onUpdateComment(comment = text?.toString())
        }

        // Submit rating
        binding.navigateButton.setOnClickListener {
            ratingViewModel.onSubmitRating()
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

        // Navigate to complete screen if rating is successfully submitted.
        viewLifecycleOwner.lifecycleScope.launch {
            ratingViewModel.ratingUiSubmitState.collect { uiSubmitState ->
                if (uiSubmitState is RatingUiState.Success) {
                    findNavController(R.id.ratingCommentFragment)?.navigate(
                        RatingCommentFragmentDirections
                            .actionRatingCommentFragmentToRatingCompleteFragment()
                    )
                }
            }
        }

        return binding.root
    }
}