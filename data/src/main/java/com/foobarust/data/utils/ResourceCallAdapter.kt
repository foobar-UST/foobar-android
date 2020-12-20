package com.foobarust.data.utils

import com.foobarust.domain.states.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * Created by kevin on 12/18/20
 */

class ResourceCallAdapter<T>(
    private val successType: Type
) : CallAdapter<T, Call<Resource<T>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<Resource<T>> {
        return ResourceCall(call)
    }
}