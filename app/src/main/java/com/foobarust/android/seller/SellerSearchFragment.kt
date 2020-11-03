package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSearchBinding
import com.foobarust.android.utils.AutoClearedValue

class SellerSearchFragment : DialogFragment() {

    private var binding: FragmentSellerSearchBinding by AutoClearedValue(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set full screen dialog theme
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight_Search)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerSearchBinding.inflate(inflater, container, false)

        // Click scrim to dismiss dialog
        binding.scrimView.setOnClickListener { dismiss() }

        // Clear text
        binding.clearTextButton.setOnClickListener {
            with(binding.searchEditText.text) {
                if (isEmpty()) dismiss() else clear()
            }
        }

        return binding.root
    }
}