package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerDetailBinding
import com.foobarust.android.utils.AutoClearedValue

/**
 * Created by kevin on 9/24/20
 */

class SellerDetailFragment : DialogFragment() {

    private var binding: FragmentSellerDetailBinding by AutoClearedValue(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerDetailBinding.inflate(inflater, container, false)

        return binding.root
    }
}