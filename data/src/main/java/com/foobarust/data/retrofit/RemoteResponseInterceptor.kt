package com.foobarust.data.retrofit

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by kevin on 1/6/21
 */

class RemoteResponseInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val code = response.code

        return if (code in 400..599) {
            // Interceptor handles only IOExceptions.
            // Issue: https://github.com/gildor/kotlin-coroutines-retrofit/issues/34
            throw IOException(parseErrorResponse(response))
        } else {
            response
        }
    }

    @Throws(Exception::class)
    private fun parseErrorResponse(response: Response): String {
        val errorResponse = Gson().fromJson(response.body?.string(), ErrorResponse::class.java)
        return errorResponse.error.toString()
    }
}