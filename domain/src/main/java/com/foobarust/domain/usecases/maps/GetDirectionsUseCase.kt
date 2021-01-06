package com.foobarust.domain.usecases.maps

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.repositories.MapRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 1/6/21
 */

class GetDirectionsUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetDirectionsParameters, List<Geolocation>>(coroutineDispatcher) {

    override suspend fun execute(parameters: GetDirectionsParameters): List<Geolocation> {
        return mapRepository.getDirectionsPath(
            originLatitude = parameters.sellerLatitude,
            originLongitude = parameters.sellerLongitude,
            destLatitude = 22.33776,
            destLongitude = 114.26364
        )
    }
}

data class GetDirectionsParameters(
    val sellerLatitude: Double,
    val sellerLongitude: Double
)