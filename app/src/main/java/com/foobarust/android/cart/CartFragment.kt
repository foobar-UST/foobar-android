package com.foobarust.android.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentCartBinding
import com.foobarust.android.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 10/29/20
 */

@AndroidEntryPoint
class CartFragment : DialogFragment() {

    private var binding: FragmentCartBinding by AutoClearedValue(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // Close cart button
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        return binding.root
    }
}