package com.foobarust.domain.repository

import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.models.cart.UpdateUserCartItem
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Created by kevin on 4/19/21
 */

class FakeCartRepositoryImpl(
    private val userId: String,
    private val idToken: String,
    private val sellerId: String,
    private val sectionId: String? = null,
) : CartRepository {

    private var shouldReturnNetworkError = false
    private var shouldReturnDiffItemSeller = false

    private val _cartItems = MutableStateFlow<List<UserCartItem>>(emptyList())

    private val _userCart = MutableStateFlow(buildFakeUserCart())

    override fun getUserCartObservable(userId: String): Flow<Resource<UserCart>> = flow {
        emit(Resource.Loading())
        if (shouldReturnNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emitAll(_userCart.map { Resource.Success(it) })
        }
    }

    override suspend fun clearUserCart(idToken: String) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        _userCart.value = buildFakeUserCart()
    }

    override suspend fun syncUserCart(idToken: String) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
    }

    override fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>> = flow {
        emit(Resource.Loading())
        if (shouldReturnNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emitAll(_cartItems.map { Resource.Success(it) })
        }
    }

    override suspend fun addUserCartItem(idToken: String, addUserCartItem: AddUserCartItem) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        if (this.idToken != idToken) throw Exception("Invalid id token.")
        if (shouldReturnDiffItemSeller) throw Exception("Attempt to add item to cart from a different seller.")

        val newCartItems = _cartItems.value.toMutableList()
        newCartItems.add(UserCartItem(
            id = UUID.randomUUID().toString(),
            itemId = addUserCartItem.itemId,
            itemSellerId = sellerId,
            itemSectionId = sectionId,
            itemTitle = "item title",
            itemTitleZh = "item title zh",
            itemPrice = 20.0,
            itemImageUrl = "about:blank",
            amounts = 1,
            totalPrice = 20.0,
            available = true,
            updatedAt = Date()
        ))
        _cartItems.value = newCartItems

        val newSubtotalCost = newCartItems.sumOf { it.totalPrice }
        _userCart.value = _userCart.value.copy(
            itemsCount = newCartItems.size,
            subtotalCost = newSubtotalCost,
            totalCost = newSubtotalCost + _userCart.value.deliveryCost,
            updatedAt = Date()
        )
    }

    override suspend fun updateUserCartItem(
        idToken: String,
        updateUserCartItem: UpdateUserCartItem
    ) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        if (this.idToken != idToken) throw Exception("Invalid id token.")
        if (shouldReturnDiffItemSeller) throw Exception("Attempt to add item to cart from a different seller.")

        val selectedItem = _cartItems.value.firstOrNull {
            it.id == updateUserCartItem.cartItemId
        } ?: throw Exception("Not cart item with respected id.")

        val newCartItems = _cartItems.value.toMutableList()
        newCartItems.add(UserCartItem(
            id = selectedItem.id,
            itemId = selectedItem.itemId,
            itemSellerId = selectedItem.itemSellerId,
            itemSectionId = selectedItem.itemSectionId,
            itemTitle = selectedItem.itemTitle,
            itemTitleZh = selectedItem.itemTitleZh,
            itemPrice = selectedItem.itemPrice,
            itemImageUrl = selectedItem.itemImageUrl,
            amounts = selectedItem.amounts,
            totalPrice = selectedItem.totalPrice,
            available = selectedItem.available,
            updatedAt = selectedItem.updatedAt
        ))
        _cartItems.value = newCartItems

        val newSubtotalCost = newCartItems.sumOf { it.totalPrice }
        _userCart.value = _userCart.value.copy(
            itemsCount = newCartItems.size,
            subtotalCost = newSubtotalCost,
            totalCost = newSubtotalCost + _userCart.value.deliveryCost,
            updatedAt = Date()
        )
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun buildFakeUserCart(): UserCart = UserCart(
        title = "cart title",
        titleZh = "cart title",
        userId = userId,
        sellerId = sellerId,
        sellerName = "seller name",
        sellerNameZh = "seller name",
        sellerType = if (sectionId != null) SellerType.OFF_CAMPUS else SellerType.ON_CAMPUS,
        sectionId = sectionId,
        sectionTitle = if (sectionId != null) "section title" else null,
        sectionTitleZh = if (sectionId != null) "section title" else null,
        deliveryTime = if (sectionId != null) Date() else null,
        imageUrl = "about:blank",
        pickupLocation = Geolocation(
            address = "address",
            addressZh = "address",
            locationPoint = GeolocationPoint(1.0, 2.0)
        ),
        itemsCount = 0,
        subtotalCost = 0.toDouble(),
        deliveryCost = 0.toDouble(),
        totalCost = 0.toDouble(),
        syncRequired = false,
        updatedAt = Date()
    )
}