package com.foobarust.data.repositories

import com.foobarust.data.api.RemoteService
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USER_CARTS_COLLECTION
import com.foobarust.data.common.Constants.USER_CART_ITEMS_SUB_COLLECTION
import com.foobarust.data.mappers.CartMapper
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.models.cart.UpdateUserCartItem
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
            .snapshotFlow(cartMapper::toUserCart, keepAlive = true)
    }

    override fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>> {
        return firestore.collection(
            "$USERS_COLLECTION/$userId/$USER_CART_ITEMS_SUB_COLLECTION"
        )
            .snapshotFlow(cartMapper::toUserCartItem)
    }

    override suspend fun addUserCartItem(idToken: String, addUserCartItem: AddUserCartItem) {
        val request = cartMapper.toAddUserCartItemRequest(addUserCartItem)
        remoteService.addUserCartItem(
            idToken = idToken,
            addUserCartItemRequest = request
        )
    }

    override suspend fun updateUserCartItem(idToken: String, updateUserCartItem: UpdateUserCartItem) {
        val request = cartMapper.toUpdateUserCartItemRequest(updateUserCartItem)
        remoteService.removeUserCartItem(
            idToken = idToken,
            updateUserCartItemRequest = request
        )
    }

    override suspend fun clearUserCart(idToken: String) {
        remoteService.clearUserCart(idToken)
    }

    override suspend fun syncUserCart(idToken: String) {
        remoteService.syncUserCart(idToken)
    }
}