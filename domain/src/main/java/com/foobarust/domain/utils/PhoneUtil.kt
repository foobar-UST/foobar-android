package com.foobarust.domain.utils

import javax.inject.Inject

class PhoneUtil @Inject constructor() {

    fun getPhoneNumPrefixString(): String {
        return "+$AREA_CODE"
    }

    fun getFormattedPhoneNumString(phoneNum: String): String {
        return "${getPhoneNumPrefixString()} ${phoneNum.substring(0, 4)} ${phoneNum.substring(4, 8)}"
    }

    companion object {
        private const val AREA_CODE = 852
    }
}