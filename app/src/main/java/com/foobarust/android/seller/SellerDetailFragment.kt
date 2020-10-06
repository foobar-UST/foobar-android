package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerDetailBinding
import com.foobarust.android.utils.AppBarStateChangedListener
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.doOnOffsetChanged
import com.foobarust.android.utils.showShortToast
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 9/24/20
 */

@AndroidEntryPoint
class SellerDetailFragment : DialogFragment(), Toolbar.OnMenuItemClickListener {

    private var binding: FragmentSellerDetailBinding by AutoClearedValue(this)
    private val viewModel: SellerDetailViewModel by viewModels()
    private val args: SellerDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // TODO: Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        // Issue: https://github.com/material-components/material-components-android/issues/1310
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

        // Receive seller id argument
        viewModel.onFetchSellerDetail(sellerId = args.sellerId)

        // Set up tab layout and view pager when seller detail is successfully fetched
        viewModel.sellerDetail.observe(viewLifecycleOwner) {
            val catalogPagerAdapter = SellerCatalogPagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                sellerCatalogs = it.catalogs
            )

            binding.itemsViewPager.adapter = catalogPagerAdapter

            TabLayoutMediator(binding.categoryTabLayout, binding.itemsViewPager) { tab, position ->
                tab.text = it.catalogs[position].name
            }.attach()
        }

        // Show toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Load at most two consecutive pages for view pager
        binding.itemsViewPager.offscreenPageLimit = 1

        // Show toolbar title only when collapsed
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.doOnOffsetChanged().collect {
                viewModel.onShowToolbarTitleChanged(
                    isShow = it == AppBarStateChangedListener.State.COLLAPSED
                )
            }
        }

        // Navigation back arrow button
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        // Setup toolbar menu
        binding.toolbar.setOnMenuItemClickListener(this)

        return binding.root
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_seller_metadata -> showShortToast("Seller Metadata.")
        }

        return true
    }
}