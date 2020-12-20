package com.foobarust.data.repositories

import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USER_CARTS_COLLECTION
import com.foobarust.data.common.Constants.USER_CART_ITEMS_SUB_COLLECTION
import com.foobarust.data.mappers.CartMapper
import com.foobarust.data.models.cart.AddUserCartItemRequest
import com.foobarust.data.models.cart.RemoveUserCartItemRequest
import com.foobarust.data.remoteapi.RemoteService
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val remoteService: RemoteService,
    private val cartMapper: CartMapper
) : CartRepository {

    override fun getUserCartObservable(userId: String): Flow<Resource<UserCart>> {
        return firestore.document("$USER_CARTS_COLLECTION/$userId")
            .snapshotFlow(cartMapper::toUserCart, keepAliveUntilCreated = true)
    }

    override fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>> {
        return firestore.collection(
            "$USERS_COLLECTION/$userId/$USER_CART_ITEMS_SUB_COLLECTION"
        )
            .snapshotFlow(cartMapper::toUserCartItem)
    }

    override suspend fun addUserCartItem(
        idToken: String,
        sellerId: String,
        itemId: String,
        amounts: Int
    ): Resource<Unit> {
        val result = remoteService.addUserCartItem(
            idToken = idToken,
            addUserCartItemRequest = AddUserCartItemRequest(sellerId, itemId, amounts)
        )

        return when (result) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun removeUserCartItem(idToken: String, cartItemId: String): Resource<Unit> {
       val result = remoteService.removeUserCartItem(
            idToken = idToken,
            removeUserCartItemRequest = RemoveUserCartItemRequest(cartItemId)
        )

        return when (result) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun clearUserCart(idToken: String): Resource<Unit> {
        return when (val result = remoteService.clearUserCart(idToken)) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun syncUserCart(idToken: String): Resource<Unit> {
        return when (val result = remoteService.syncUserCart(idToken)) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }
}