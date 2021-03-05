package com.foobarust.data.retrofit

import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

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

class SuccessResponseConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        // Data<HelloWorld>
        val dataType = TypeToken.getParameterized(SuccessResponse::class.java, type).type

        // From ResponseBody to Data<T>
        val converter: Converter<ResponseBody, SuccessResponse<Any>> = retrofit.nextResponseBodyConverter(
            this,
            dataType,
            annotations
        )

        return SuccessResponseConverter(converter)
    }
}