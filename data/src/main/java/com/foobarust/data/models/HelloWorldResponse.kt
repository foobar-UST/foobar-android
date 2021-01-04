package com.foobarust.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 1/4/21
 */

data class HelloWorldResponse(
    @SerializedName("result")
    val result: String
)