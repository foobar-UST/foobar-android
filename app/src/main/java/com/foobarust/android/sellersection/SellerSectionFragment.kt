package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentSellerSectionBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerSectionBinding by AutoClearedValue(this)
    private val navArgs: SellerSectionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionBinding.inflate(inflater, container, false)

        // Navigate to SellerDetailFragment
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.setGraph(
            R.navigation.navigation_seller_section,
            SellerSectionDetailFragmentArgs(
                sellerId = navArgs.sellerId,
                sectionId = navArgs.sectionId
            ).toBundle()
        )

        // Setup AppBar configuration
        /*
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.sellerSectionDetailFragment,
            R.id.sellerSectionUsersFragment
        ))

        binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)

         */

        return binding.root
    }
}