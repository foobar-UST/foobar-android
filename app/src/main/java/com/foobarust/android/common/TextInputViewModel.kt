package com.foobarust.android.common

import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.foobarust.domain.usecases.common.GetFormattedPhoneNumUseCase
import kotlinx.parcelize.Parcelize

class TextInputViewModel @ViewModelInject constructor() : ViewModel() {

    private var _textInputProperty: TextInputProperty? = null
    var inputValue: String = ""

    fun onUpdateTextInputProperty(property: TextInputProperty) {
        _textInputProperty = property
    }

    fun isInputValid(): Boolean {
        return when (_textInputProperty?.type) {
            TextInputType.NORMAL -> inputValue.isNotBlank()
            TextInputType.NAME -> inputValue.isNotBlank()
            TextInputType.PHONE_NUM -> inputValue.length == GetFormattedPhoneNumUseCase.LENGTH
            null -> false
        }
    }
}

@Parcelize
data class TextInputProperty(
    val id: String,
    val title: String,
    var value: String? = null,
    val type: TextInputType
) : Parcelable

enum class TextInputType {
    NORMAL,                 // Normal text
    NAME,                   // Capitalize words
    PHONE_NUM               // 8 digits
}