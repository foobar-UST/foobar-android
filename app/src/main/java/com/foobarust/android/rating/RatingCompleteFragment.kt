package com.foobarust.android.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentRatingCompleteBinding
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 2/24/21
 */

@AndroidEntryPoint
class RatingCompleteFragment : Fragment() {

    private var binding: FragmentRatingCompleteBinding by AutoClearedValue(this)
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
        binding = FragmentRatingCompleteBinding.inflate(inflater, container, false)

        // Complete button
        binding.completeButton.setOnClickListener {
            ratingViewModel.onCompleteRating()
        }

        return binding.root
    }
}