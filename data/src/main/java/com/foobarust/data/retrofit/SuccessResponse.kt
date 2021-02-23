package com.foobarust.data.retrofit

import com.foobarust.data.constants.Constants.REMOTE_SUCCESS_RESPONSE_DATA_OBJECT
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 1/10/21
 */

data class SuccessResponse<T>(
    @SerializedName(REMOTE_SUCCESS_RESPONSE_DATA_OBJECT)
    val data: T
)