package com.foobarust.domain.repository

import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repositories.PromotionRepository
import com.foobarust.domain.serialize.AdvertiseSerialized
import com.foobarust.domain.serialize.toAdvertiseBasic
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Created by kevin on 4/9/21
 */

class FakePromotionRepositoryImpl : PromotionRepository {

    internal val advertiseList: List<AdvertiseSerialized> by lazy {
        deserializeJsonList("advertises_fake_data.json")
    }

    private var shouldReturnNetworkError = false

    override suspend fun getAdvertiseBasics(
        sellerType: SellerType,
        numOfAdvertises: Int
    ): List<AdvertiseBasic> {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return advertiseList.take(numOfAdvertises)
            .map { it.toAdvertiseBasic() }
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private inline fun<reified T> deserializeJsonList(file: String): List<T> {
        val inputStream = javaClass.classLoader.getResourceAsStream(file)!!
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString(jsonString)
    }
}