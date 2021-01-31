package com.foobarust.android.seller

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by kevin on 10/11/20
 */

class SellerPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val sellerPages: List<SellerPage>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = sellerPages.size

    override fun createFragment(position: Int): Fragment {
        return sellerPages[position].fragment()
    }
}

data class SellerPage(
    val tag: String,
    val title: String,
    val fragment: () -> Fragment
)