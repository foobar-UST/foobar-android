package com.foobarust.android.sellermisc

import androidx.hilt.lifecycle.ViewModelInject
import com.foobarust.android.common.BaseViewModel

/**
 * Created by kevin on 10/11/20
 */

class SellerMiscViewModel @ViewModelInject constructor() : BaseViewModel() {

    lateinit var miscProperty: SellerMiscProperty
        private set

    fun setMiscProperty(miscProperty: SellerMiscProperty) {
        this.miscProperty = miscProperty
    }
}
