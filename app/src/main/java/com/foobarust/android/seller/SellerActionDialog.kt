package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.databinding.DialogSellerActionBinding
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView

/**
 * Created by kevin on 10/18/20
 */

class SellerActionDialog : BottomSheetDialogFragment(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: DialogSellerActionBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSellerActionBinding.inflate(inflater, container, false)

        // Setup navigation view
        binding.navigationView.setNavigationItemSelectedListener(this)

        return binding.root
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_seller_add_favorite -> {
                // TODO: Add seller id to favorite
            }
            R.id.action_seller_ratings -> {
                // TODO: Navigate to rating
            }
            R.id.action_seller_feedback -> {
                // TODO: Open email intent
            }
        }

        return findNavController().navigateUp()
    }
}