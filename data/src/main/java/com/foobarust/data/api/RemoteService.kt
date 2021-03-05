package com.foobarust.data.api

import com.foobarust.data.constants.Constants.REMOTE_AUTH_HEADER
import com.foobarust.data.constants.Constants.SEARCH_SELLERS_REQUEST_SEARCH_QUERY
import com.foobarust.data.models.cart.AddUserCartItemRequest
import com.foobarust.data.models.cart.UpdateUserCartItemRequest
import com.foobarust.data.models.checkout.PlaceOrderRequest
import com.foobarust.data.models.checkout.PlaceOrderResponse
import com.foobarust.data.models.order.SubmitOrderRatingRequest
import com.foobarust.data.models.seller.SearchSellerResponse
import com.foobarust.data.models.user.InsertDeviceTokenRequest
import com.foobarust.data.models.user.LinkDeviceTokenRequest
import com.foobarust.data.models.user.UnlinkDeviceTokenRequest
import com.foobarust.data.models.user.UpdateUserDetailRequest
import retrofit2.http.*

/**
 * Created by kevin on 12/13/20
 */

interface RemoteService {

    @POST("user/")
    suspend fun updateUserDetail(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body updateUserDetailRequest: UpdateUserDetailRequest
    )

    @PUT("device/add")
    suspend fun insertDeviceToken(
        @Body insertDeviceTokenRequest: InsertDeviceTokenRequest
    )

    @POST("device/link")
    suspend fun linkDeviceToken(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body linkDeviceTokenRequest: LinkDeviceTokenRequest
    )

    @POST("device/unlink")
    suspend fun unlinkDeviceToken(
        @Body unlinkDeviceTokenRequest: UnlinkDeviceTokenRequest
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

    @GET("seller/search")
    suspend fun searchSellers(
        @Query(SEARCH_SELLERS_REQUEST_SEARCH_QUERY) searchQuery: String
    ): List<SearchSellerResponse>

    @PUT("order/rate")
    suspend fun submitOrderRating(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body submitOrderRatingRequest: SubmitOrderRatingRequest
    )
}