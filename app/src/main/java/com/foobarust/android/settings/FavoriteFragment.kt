package com.foobarust.android.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentFavoriteBinding
import com.foobarust.android.utils.AutoClearedValue

class FavoriteFragment : FullScreenDialogFragment() {

    private var binding: FragmentFavoriteBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        // Dismiss dialog
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}