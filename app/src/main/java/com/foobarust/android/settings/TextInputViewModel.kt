package com.foobarust.android.settings

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.foobarust.domain.usecases.common.GetFormattedPhoneNumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class TextInputViewModel @Inject constructor() : ViewModel() {

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