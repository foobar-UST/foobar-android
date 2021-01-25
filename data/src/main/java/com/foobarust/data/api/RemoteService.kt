package com.foobarust.data.api

import com.foobarust.data.common.Constants.REMOTE_AUTH_HEADER
import com.foobarust.data.models.cart.AddUserCartItemRequest
import com.foobarust.data.models.cart.UpdateUserCartItemRequest
import com.foobarust.data.models.checkout.PlaceOrderRequest
import com.foobarust.data.models.checkout.PlaceOrderResponse
import com.foobarust.data.models.common.HelloWorldResponse
import com.foobarust.data.models.user.UpdateUserDetailRequest
import retrofit2.http.*

/**
 * Created by kevin on 12/13/20
 */

interface RemoteService {

    @GET("test/hello-world")
    suspend fun getHelloWorld(
        @Query("has_error") hasError: Boolean
    ): HelloWorldResponse

    @POST("user/")
    suspend fun updateUserDetail(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body updateUserDetailRequest: UpdateUserDetailRequest
    )

    @PUT("cart/")
    suspend fun addUserCartItem(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body addUserCartItemRequest: AddUserCartItemRequest
    )

    @POST("cart/")
    suspend fun removeUserCartItem(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body updateUserCartItemRequest: UpdateUserCartItemRequest
    )

    @DELETE("cart/")
    suspend fun clearUserCart(
        @Header(REMOTE_AUTH_HEADER) idToken: String
    )

    @POST("cart/sync")
    suspend fun syncUserCart(
        @Header(REMOTE_AUTH_HEADER) idToken: String
    )

    @PUT("order/")
    suspend fun placeOrder(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body placeOrderRequest: PlaceOrderRequest
    ): PlaceOrderResponse
}