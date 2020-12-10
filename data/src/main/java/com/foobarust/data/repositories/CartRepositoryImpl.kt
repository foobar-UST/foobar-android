package com.foobarust.data.repositories

import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USER_CARTS_COLLECTION
import com.foobarust.data.common.Constants.USER_CART_ITEMS_AMOUNTS_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_SUB_COLLECTION
import com.foobarust.data.common.Constants.USER_CART_ITEMS_UPDATE_PRICE_REQUIRED_FIELD
import com.foobarust.data.mappers.CartMapper
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cartMapper: CartMapper
) : CartRepository {

    override fun getUserCartObservable(userId: String): Flow<Resource<UserCart>> {
        return firestore.collection(USER_CARTS_COLLECTION)
            .document(userId)
            .snapshotFlow(cartMapper::toUserCart)
    }

    override fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>> {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(USER_CART_ITEMS_SUB_COLLECTION)
            .snapshotFlow(cartMapper::toUserCartItem)
    }

    // TODO: Migrate to backend
    override suspend fun addUserCartItem(userId: String, userCartItem: UserCartItem) {
        // TODO: increment amount
        val newCartItemEntity = cartMapper.toUserCartItemEntity(userCartItem)

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(USER_CART_ITEMS_SUB_COLLECTION)
            .document(userCartItem.id)
            .set(newCartItemEntity)
            .await()
    }

    // TODO: Migrate to backend
    override suspend fun removeUserCartItem(userId: String, cartItemId: String) {
        val networkDocument = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(USER_CART_ITEMS_SUB_COLLECTION)
            .document(cartItemId)

        val networkCartItem = networkDocument.getAwaitResult(cartMapper::toUserCartItem)

        // Reduce the amounts or delete the cart item
        if (networkCartItem.amounts > 1) {
            networkDocument.update(mapOf(
                USER_CART_ITEMS_AMOUNTS_FIELD to networkCartItem.amounts - 1,
                USER_CART_ITEMS_UPDATE_PRICE_REQUIRED_FIELD to true
            ))
        } else {
            networkDocument.delete().await()
        }
    }

    // TODO: Migrate to backend
    override suspend fun clearUserCart(userId: String) {
        val cartCollection = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(USER_CART_ITEMS_SUB_COLLECTION)

        cartCollection.getAwaitResult(cartMapper::toUserCartItem)
            .forEach { cartCollection.document(it.id).delete() }
    }
}