package com.foobarust.android.checkout

import android.content.Context
import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.parcelize.Parcelize

/**
 * Created by kevin on 1/10/21
 */

class DeliveryOptionsViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _optionsListModels = MutableLiveData<List<DeliveryOptionsItemModel>>()
    val optionsListModels: LiveData<List<DeliveryOptionsItemModel>>
        get() = _optionsListModels

    fun onBuildOptionsListModels(properties: Array<DeliveryOptionProperty>) {
        _optionsListModels.value = buildList {
            val optionsItemModels = properties.mapIndexed { index, property ->
                property.toDeliveryOptionsItemModel(
                    context = context,
                    isDefault = index == 0
                )
            }

            addAll(optionsItemModels)
        }
    }
}

@Parcelize
data class DeliveryOptionProperty(
    val id: String,
    val identifier: String
) : Parcelable