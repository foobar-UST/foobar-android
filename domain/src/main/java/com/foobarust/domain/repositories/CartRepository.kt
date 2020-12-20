package com.foobarust.domain.repositories

import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getUserCartObservable(userId: String): Flow<Resource<UserCart>>

    fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>>

    suspend fun addUserCartItem(
        idToken: String,
        sellerId: String,
        itemId: String,
        amounts: Int
    ): Resource<Unit>

    suspend fun removeUserCartItem(idToken: String, cartItemId: String): Resource<Unit>

    suspend fun clearUserCart(idToken: String): Resource<Unit>

    suspend fun syncUserCart(idToken: String): Resource<Unit>
}