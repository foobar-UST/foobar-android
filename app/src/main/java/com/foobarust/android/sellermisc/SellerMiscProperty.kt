package com.foobarust.android.sellermisc

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by kevin on 11/3/20
 */

@Parcelize
data class SellerMiscProperty(
    val sellerName: String,
    val email: String,
    val description: String,
    val phoneNum: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String
) : Parcelable