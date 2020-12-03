package com.foobarust.android.sellermisc

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by kevin on 11/3/20
 */

@Parcelize
data class SellerMiscProperty(
    val name: String,
    val description: String?,
    val address: String,
    val phoneNum: String,
    val website: String?,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String
) : Parcelable