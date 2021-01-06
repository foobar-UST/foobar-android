package com.foobarust.data.retrofit

import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * Created by kevin on 1/4/21
 */

data class ResourceData<T>(val data: T)

class ResourceConverter(
    private val delegate: Converter<ResponseBody, ResourceData<Any>>
) : Converter<ResponseBody, Any> {

    override fun convert(value: ResponseBody): Any? {
        return delegate.convert(value)?.data
    }
}