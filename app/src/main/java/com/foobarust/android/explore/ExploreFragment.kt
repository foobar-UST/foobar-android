package com.foobarust.android.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.foobarust.android.databinding.FragmentExploreBinding
import com.foobarust.android.utils.AutoClearedValue

class ExploreFragment : Fragment() {

    private var binding: FragmentExploreBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        return binding.root
    }
}