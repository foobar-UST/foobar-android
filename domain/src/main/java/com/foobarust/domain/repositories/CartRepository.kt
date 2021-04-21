package com.foobarust.domain.repositories

import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.models.cart.UpdateUserCartItem
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getUserCartObservable(userId: String): Flow<Resource<UserCart>>

    suspend fun clearUserCart(idToken: String)

    suspend fun syncUserCart(idToken: String)

    fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>>

    suspend fun addUserCartItem(idToken: String, addUserCartItem: AddUserCartItem)

    suspend fun updateUserCartItem(idToken: String, updateUserCartItem: UpdateUserCartItem)
}