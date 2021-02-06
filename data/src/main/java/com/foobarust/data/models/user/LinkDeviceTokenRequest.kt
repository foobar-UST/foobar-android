package com.foobarust.data.models.user

import com.foobarust.data.common.Constants.LINK_DEVICE_TOKEN_REQUEST_TOKEN
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 2/6/21
 */

data class LinkDeviceTokenRequest(
    @SerializedName(LINK_DEVICE_TOKEN_REQUEST_TOKEN)
    val deviceToken: String
)