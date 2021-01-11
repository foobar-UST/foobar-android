package com.foobarust.data.retrofit

import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * Created by kevin on 1/4/21
 */



class SuccessResponseConverter(
    private val delegate: Converter<ResponseBody, SuccessResponse<Any>>
) : Converter<ResponseBody, Any> {

    override fun convert(value: ResponseBody): Any? {
        return delegate.convert(value)?.data
    }
}