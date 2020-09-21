package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.showShortToast

class SellerFragment : Fragment(), PromotionBannerAdapter.PromotionBannerAdapterListener {

    private var binding: FragmentSellerBinding by AutoClearedValue(this)
    private val viewModel: SellerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerBinding.inflate(inflater, container, false)

        // Setup promotion banner
        val promotionAdapter = PromotionBannerAdapter(this)
        binding.promotionViewPager.apply {
            setAdapter(promotionAdapter)
            setLifecycleRegistry(lifecycle)

            // Banner item margin
            val margin = resources.getDimensionPixelOffset(R.dimen.spacing_xmedium)
            setPageMargin(margin)
            setRevealWidth(margin, margin)

            // Indicator
            setIndicatorView(binding.promotionIndicator)

            removeDefaultPageTransformer()
        }

        viewModel.promotionItems.observe(viewLifecycleOwner) {
            binding.promotionViewPager.create(it)
        }


        return binding.root
    }

    override fun onPromotionBannerItemClicked(promotionItem: PromotionItem) {
        showShortToast("Promotion clicked.")
    }
}