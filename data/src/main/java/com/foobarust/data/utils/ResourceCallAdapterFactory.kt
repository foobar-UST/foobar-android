package com.foobarust.data.utils

import com.foobarust.domain.states.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by kevin on 12/18/20
 */

class ResourceCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        check(returnType is ParameterizedType) {
            "Return type must be parameterized"
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != Resource::class.java) {
            return null
        }

        check(responseType is ParameterizedType) {
            "Response type must be parameterized"
        }

        val successBodyType = getParameterUpperBound(0, responseType)

        return ResourceCallAdapter<Any>(successBodyType)
    }
}