package com.foobarust.data.retrofit

import com.foobarust.domain.states.Resource
import com.google.gson.Gson
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by kevin on 12/18/20
 */

internal class ResourceCall<T>(private val delegate: Call<T>) : Call<Resource<T>> {

    override fun clone(): Call<Resource<T>> = ResourceCall(delegate.clone())

    override fun execute(): Response<Resource<T>> {
        throw UnsupportedOperationException("ResourceCall doesn't support execute")
    }

    override fun enqueue(callback: Callback<Resource<T>>) {
        return delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                //val code = response.code()

                if (response.isSuccessful) {
                    if (body != null) {
                        // Success response
                        callback.onResponse(
                            this@ResourceCall,
                            Response.success(Resource.Success(body))
                        )
                    } else {
                        // Empty response
                        callback.onResponse(
                            this@ResourceCall,
                            Response.success(Resource.Error("Empty response."))
                        )
                    }
                } else {
                    val errorBody = response.errorBody()
                    val error = when {
                        errorBody == null -> null
                        errorBody.contentLength() == 0L -> null
                        else -> convertErrorBody(errorBody)
                    }

                    callback.onResponse(
                        this@ResourceCall,
                        Response.success(Resource.Error(error))
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@ResourceCall,
                    Response.success(Resource.Error(t.message))
                )
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled() = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private fun convertErrorBody(errorBody: ResponseBody): String {
        val errorResponse = Gson().fromJson(errorBody.string(), ErrorResponse::class.java)
        return errorResponse.error.toString()
    }
}