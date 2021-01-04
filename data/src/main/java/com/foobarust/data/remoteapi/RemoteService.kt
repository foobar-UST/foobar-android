package com.foobarust.data.remoteapi

import com.foobarust.data.common.Constants.CF_AUTH_HEADER
import com.foobarust.data.models.HelloWorldResponse
import com.foobarust.data.models.cart.AddUserCartItemRequest
import com.foobarust.data.models.cart.UpdateUserCartItemRequest
import com.foobarust.domain.states.Resource
import retrofit2.http.*

/**
 * Created by kevin on 12/13/20
 */

interface RemoteService {

    @GET("test/hello-world")
    suspend fun getHelloWorld(
        @Query("has_error") hasError: Boolean
    ) : Resource<HelloWorldResponse>

    @PUT("cart/")
    suspend fun addUserCartItem(
        @Header(CF_AUTH_HEADER) idToken: String,
        @Body addUserCartItemRequest: AddUserCartItemRequest
    ): Resource<Unit>

    @POST("cart/")
    suspend fun removeUserCartItem(
        @Header(CF_AUTH_HEADER) idToken: String,
        @Body updateUserCartItemRequest: UpdateUserCartItemRequest
    ): Resource<Unit>

    @DELETE("cart/")
    suspend fun clearUserCart(
        @Header(CF_AUTH_HEADER) idToken: String
    ): Resource<Unit>

    @POST("cart/sync")
    suspend fun syncUserCart(
        @Header(CF_AUTH_HEADER) idToken: String
    ): Resource<Unit>
}