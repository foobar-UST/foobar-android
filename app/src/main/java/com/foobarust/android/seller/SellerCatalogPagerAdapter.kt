package com.foobarust.android.seller

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.foobarust.domain.models.SellerCatalog

/**
 * Created by kevin on 10/4/20
 */

class SellerCatalogPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val sellerCatalogs: List<SellerCatalog>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = sellerCatalogs.size

    override fun createFragment(position: Int): Fragment {
        return SellerItemsFragment.newInstance(
            categoryId = sellerCatalogs[position].id
        )
    }
}