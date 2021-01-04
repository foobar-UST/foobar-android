package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSectionParticipantsBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionParticipantsFragment : Fragment() {

    private var binding: FragmentSellerSectionParticipantsBinding by AutoClearedValue(this)
    private val sectionViewModel: SellerSectionViewModel by navGraphViewModels(R.id.navigation_seller_section)
    private val participantsViewModel: SellerSectionParticipantsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionParticipantsBinding.inflate(inflater, container, false)

        // Observe dialog back press and navigate up
        sectionViewModel.backPressed.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}