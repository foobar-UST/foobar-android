package com.foobarust.data.remoteapi

import com.foobarust.data.common.Constants.AUTHORIZATION_HEADER
import com.foobarust.data.models.cart.AddUserCartItemRequest
import com.foobarust.data.models.cart.RemoveUserCartItemRequest
import com.foobarust.domain.states.Resource
import retrofit2.http.*

/**
 * Created by kevin on 12/13/20
 */

interface RemoteService {

    @PUT("cart/")
    suspend fun addUserCartItem(
        @Header(AUTHORIZATION_HEADER) idToken: String,
        @Body addUserCartItemRequest: AddUserCartItemRequest
    ): Resource<Unit>

    @POST("cart/")
    suspend fun removeUserCartItem(
        @Header(AUTHORIZATION_HEADER) idToken: String,
        @Body removeUserCartItemRequest: RemoveUserCartItemRequest
    ): Resource<Unit>

    @DELETE("cart/")
    suspend fun clearUserCart(
        @Header(AUTHORIZATION_HEADER) idToken: String
    ): Resource<Unit>

    @POST("cart/sync")
    suspend fun syncUserCart(
        @Header(AUTHORIZATION_HEADER) idToken: String
    ): Resource<Unit>
}