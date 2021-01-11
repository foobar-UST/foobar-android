package com.foobarust.android.checkout

import android.content.Context
import com.foobarust.android.R
import com.foobarust.android.checkout.CartListModel.CartDeliveryOptionItemModel
import com.foobarust.data.common.Constants
import com.foobarust.domain.models.checkout.DeliveryOption

/**
 * Created by kevin on 1/11/21
 */

fun DeliveryOption.toDeliveryOptionProperty(): DeliveryOptionProperty {
    return DeliveryOptionProperty(
        id = id,
        identifier = identifier
    )
}

fun DeliveryOption.toCartDeliveryOptionItemModel(context: Context): CartDeliveryOptionItemModel {
    val parsedTitle = when (identifier) {
        Constants.DELIVERY_OPTION_IDENTIFIER_PICKUP -> context.getString(R.string.delivery_options_title_pickup)
        Constants.DELIVERY_OPTION_IDENTIFIER_DELIVERY -> context.getString(R.string.delivery_options_title_delivery)
        else -> throw IllegalStateException("Invalid delivery option.")
    }

    val drawableRes = when (identifier) {
        Constants.DELIVERY_OPTION_IDENTIFIER_PICKUP -> R.drawable.ic_shopping_bag
        Constants.DELIVERY_OPTION_IDENTIFIER_DELIVERY -> R.drawable.ic_local_shipping
        else -> throw IllegalStateException("Invalid delivery option.")
    }

    return CartDeliveryOptionItemModel(
        optionId = id,
        title = parsedTitle,
        drawable = drawableRes
    )
}

fun DeliveryOptionProperty.toDeliveryOptionsItemModel(
    context: Context,
    isDefault: Boolean = false
): DeliveryOptionsItemModel {
    val parsedTitle = when (identifier) {
        Constants.DELIVERY_OPTION_IDENTIFIER_PICKUP -> context.getString(R.string.delivery_options_title_pickup)
        Constants.DELIVERY_OPTION_IDENTIFIER_DELIVERY -> context.getString(R.string.delivery_options_title_delivery)
        else -> throw IllegalStateException("Invalid delivery option.")
    }

    val drawableRes = when (identifier) {
        Constants.DELIVERY_OPTION_IDENTIFIER_PICKUP -> R.drawable.ic_shopping_bag
        Constants.DELIVERY_OPTION_IDENTIFIER_DELIVERY -> R.drawable.ic_local_shipping
        else -> throw IllegalStateException("Invalid delivery option.")
    }

    return DeliveryOptionsItemModel(
        optionId = id,
        title = parsedTitle,
        drawable = drawableRes,
        isSelected = isDefault
    )
}