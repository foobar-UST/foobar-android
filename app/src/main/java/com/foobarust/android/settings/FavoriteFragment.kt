package com.foobarust.android.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.foobarust.android.databinding.FragmentFavoriteBinding
import com.foobarust.android.utils.AutoClearedValue

class FavoriteFragment : Fragment() {

    private var binding: FragmentFavoriteBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }
}