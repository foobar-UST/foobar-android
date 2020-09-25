package com.foobarust.android.common

import javax.inject.Inject

class PhoneUtil @Inject constructor() {

    fun getFormattedString(phoneNum: String): String {
        return "$AREA_CODE ${phoneNum.substring(0, 4)} ${phoneNum.substring(4, 8)}"
    }

    companion object {
        const val AREA_CODE = "+852 "
    }
}