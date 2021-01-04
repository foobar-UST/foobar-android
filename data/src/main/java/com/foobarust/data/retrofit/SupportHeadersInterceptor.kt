package com.foobarust.data.retrofit

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by kevin on 12/13/20
 */

class SupportHeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}