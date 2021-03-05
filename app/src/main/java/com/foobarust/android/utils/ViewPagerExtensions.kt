package com.foobarust.android.utils

import androidx.viewpager2.widget.ViewPager2

/**
 * Created by kevin on 2/24/21
 */

fun ViewPager2.navigateToPreviousPage(animate: Boolean = true) {
    if (currentItem > 0) {
        setCurrentItem(currentItem - 1, animate)
    }
}

fun ViewPager2.navigateToNextPage(animate: Boolean = true) {
    if (!inLastPage()) {
        setCurrentItem(currentItem + 1, animate)
    }
}

fun ViewPager2.inLastPage(): Boolean {
    return adapter?.itemCount?.let {
        currentItem == it - 1
    } ?: false
}

fun ViewPager2.inFirstPage(): Boolean = currentItem == 0