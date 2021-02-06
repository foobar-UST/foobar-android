package com.foobarust.data.models.user

import com.foobarust.data.common.Constants.INSERT_DEVICE_TOKEN_REQUEST_TOKEN
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 2/2/21
 */

data class InsertDeviceTokenRequest(
    @SerializedName(INSERT_DEVICE_TOKEN_REQUEST_TOKEN)
    val deviceToken: String
)