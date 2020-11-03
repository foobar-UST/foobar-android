package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.foobarust.android.databinding.FragmentSellerOffCampusBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerOffCampusFragment : Fragment() {

    private var binding: FragmentSellerOffCampusBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerOffCampusBinding.inflate(inflater, container, false)

        return binding.root
    }
}