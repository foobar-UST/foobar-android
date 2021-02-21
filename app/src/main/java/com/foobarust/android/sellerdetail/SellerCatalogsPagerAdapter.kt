package com.foobarust.android.sellerdetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.foobarust.android.selleritem.SellerItemsFragment
import com.foobarust.android.selleritem.SellerItemsProperty
import com.foobarust.domain.models.seller.SellerCatalog

/**
 * Created by kevin on 10/4/20
 */

class SellerCatalogsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val sellerId: String,
    private val sellerCatalogs: List<SellerCatalog>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = sellerCatalogs.size

    override fun createFragment(position: Int): Fragment {
        return SellerItemsFragment.newInstance(
            SellerItemsProperty(
                sellerId = sellerId,
                catalogId = sellerCatalogs[position].id
            )
        )
    }
}