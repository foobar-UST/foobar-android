package com.foobarust.testshared.di

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.models.user.UserDetail
import java.util.*

/**
 * Created by kevin on 4/21/21
 */
 
class DependencyContainer {

    val fakeIdToken: String = UUID.randomUUID().toString()

    val fakeAuthProfile: AuthProfile = AuthProfile(
        id = USER_ID,
        email = EMAIL,
        username = USERNAME
    )

    val fakeUserDetail: UserDetail = UserDetail(
        id = USER_ID,
        username = USERNAME,
        email = EMAIL,
        name = "Hello World",
        phoneNum = "12345678",
        photoUrl = "about:blank",
        updatedAt = Date()
    )

    val fakeUserCart: UserCart = UserCart(
        title = "cart title",
        titleZh = "cart title",
        userId = fakeUserDetail.id,
        sellerId = "kZr4pBjju7gQniYGS0kN",
        sellerName = "seller name",
        sellerNameZh = "seller name",
        sellerType = SellerType.OFF_CAMPUS,
        sectionId = "cffa66cf-d5ab-4797-aa30-eacee36d0d1f",
        sectionTitle = "section title",
        sectionTitleZh = "section title",
        deliveryTime = Date(),
        imageUrl = "about:blank",
        pickupLocation = Geolocation(
            address = "address",
            addressZh = "address",
            locationPoint = GeolocationPoint(1.0, 2.0)
        ),
        itemsCount = 2,
        subtotalCost = 0.toDouble(),
        deliveryCost = 0.toDouble(),
        totalCost = 0.toDouble(),
        syncRequired = false,
        updatedAt = Date()
    )

    val fakeUserCartItems: List<UserCartItem> = listOf(
        UserCartItem(
            id = UUID.randomUUID().toString(),
            itemId = "dfbce3d8-399e-4f3e-ab30-f092dbe57223",
            itemSellerId = fakeUserCart.sellerId,
            itemSectionId = fakeUserCart.sectionId,
            itemTitle = "Test Item 1",
            itemTitleZh = null,
            itemPrice = 44.0,
            itemImageUrl = "about:blank",
            amounts = 1,
            totalPrice = 44.0,
            available = true,
            updatedAt = Date()
        ),
        UserCartItem(
            id = UUID.randomUUID().toString(),
            itemId = "1c23c1c3-43ef-4f55-a0cb-315e6658b785",
            itemSellerId = fakeUserCart.sellerId,
            itemSectionId = fakeUserCart.sectionId,
            itemTitle = "Test Item 2",
            itemTitleZh = null,
            itemPrice = 44.0,
            itemImageUrl = "about:blank",
            amounts = 1,
            totalPrice = 44.0,
            available = true,
            updatedAt = Date()
        )
    )

    companion object {
        private val USER_ID = UUID.randomUUID().toString()
        private const val EMAIL = "testuser@foobarpp.com"
        private const val USERNAME = "testuser"
    }
}