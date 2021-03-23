package com.foobarust.domain.usecases.maps

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.repositories.MapRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 1/6/21
 */

private const val HKUST_LATITUDE = 22.33776
private const val HKUST_LONGITUDE = 114.26364

class GetDirectionsUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetDirectionsParameters, List<GeolocationPoint>>(coroutineDispatcher) {

    override suspend fun execute(parameters: GetDirectionsParameters): List<GeolocationPoint> {
        return mapRepository.getDirectionsPath(
            currentLocation = GeolocationPoint(
                latitude = HKUST_LATITUDE,
                longitude = HKUST_LONGITUDE
            ),
            destination = parameters.destination
        )
    }
}

data class GetDirectionsParameters(
    val destination: GeolocationPoint
)