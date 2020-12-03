package com.foobarust.android.sellermisc

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.sellermisc.SellerMiscListModel.*

/**
 * Created by kevin on 10/11/20
 */

class SellerMiscViewModel @ViewModelInject constructor() : BaseViewModel() {

    lateinit var miscProperty: SellerMiscProperty
        private set

    private val _sellerMiscListModels = MutableLiveData<List<SellerMiscListModel>>()
    val sellerMiscListModels: LiveData<List<SellerMiscListModel>>
        get() = _sellerMiscListModels

    fun onUpdateMiscProperty(property: SellerMiscProperty) {
        miscProperty = property
        buildSellerMiscList(property)
    }

    private fun buildSellerMiscList(property: SellerMiscProperty) {
        _sellerMiscListModels.value = buildList {
            addAll(listOf(
                SellerMiscAddressModel(
                    name = property.name,
                    address = property.address,
                ),
                SellerMiscOpeningHoursModel(
                    openingHours = property.openingHours
                ),
                SellerMiscContactModel(
                    phoneNum = property.phoneNum,
                    website = property.website
                )
            ))

            property.description?.let {
                add(SellerMiscDescriptionModel(description = it))
            }
        }
    }
}
