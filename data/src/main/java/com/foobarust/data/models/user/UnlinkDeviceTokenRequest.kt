package com.foobarust.data.models.user

import com.foobarust.data.constants.Constants.UNLINK_DEVICE_TOKEN_REQUEST_TOKEN
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 2/6/21
 */

data class UnlinkDeviceTokenRequest(
    @SerializedName(UNLINK_DEVICE_TOKEN_REQUEST_TOKEN)
    val deviceToken: String
)