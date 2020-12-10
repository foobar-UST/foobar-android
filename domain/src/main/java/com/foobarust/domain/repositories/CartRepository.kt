package com.foobarust.domain.repositories

import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getUserCartObservable(userId: String): Flow<Resource<UserCart>>

    fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>>

    suspend fun addUserCartItem(userId: String, userCartItem: UserCartItem)

    suspend fun removeUserCartItem(userId: String, cartItemId: String)

    suspend fun clearUserCart(userId: String)
}