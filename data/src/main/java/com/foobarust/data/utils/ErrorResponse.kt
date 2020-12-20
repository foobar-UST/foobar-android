package com.foobarust.data.utils

import com.foobarust.data.common.Constants.ERROR_RESPONSE_CODE_FIELD
import com.foobarust.data.common.Constants.ERROR_RESPONSE_ERROR_OBJECT
import com.foobarust.data.common.Constants.ERROR_RESPONSE_MESSAGE_FIELD
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 12/19/20
 */

data class ErrorResponse(
    @SerializedName(ERROR_RESPONSE_ERROR_OBJECT)
    val error: ErrorResponseContent
)

data class ErrorResponseContent(
    @SerializedName(ERROR_RESPONSE_CODE_FIELD)
    val code: Int,

    @SerializedName(ERROR_RESPONSE_MESSAGE_FIELD)
    val message: String?
) {
    override fun toString(): String = "Error $code: $message"
}