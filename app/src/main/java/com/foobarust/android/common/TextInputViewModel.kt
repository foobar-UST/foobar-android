package com.foobarust.android.common

import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parcelize

class TextInputViewModel @ViewModelInject constructor() : ViewModel() {

    var inputValue: String? = null
}

@Parcelize
data class TextInputProperty(
    val editId: String,
    val title: String,
    var value: String?,
    val type: TextInputType,
) : Parcelable

enum class TextInputType {
    NORMAL, NAME, PHONE_NUM
}