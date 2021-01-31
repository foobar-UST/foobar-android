package com.foobarust.android.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by kevin on 1/29/21
 */

class OrderPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val orderPages: List<OrderPage>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = orderPages.size

    override fun createFragment(position: Int): Fragment {
        return orderPages[position].fragment()
    }
}

data class OrderPage(
    val title: String,
    val fragment: () -> Fragment
)