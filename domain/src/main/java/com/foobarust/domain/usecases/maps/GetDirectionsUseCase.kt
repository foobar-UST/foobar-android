package com.foobarust.domain.usecases.maps

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.map.TravelMode
import com.foobarust.domain.repositories.MapRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/6/21
 */

class GetDirectionsUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetDirectionsParameters, List<GeolocationPoint>>(coroutineDispatcher) {

    override fun execute(parameters: GetDirectionsParameters): Flow<Resource<List<GeolocationPoint>>> = flow {
        val path = mapRepository.getDirectionsPath(
            currentLocation = parameters.currentLocation,
            destination = parameters.destination,
            travelMode = parameters.travelMode
        )

        emit(Resource.Success(path))
    }
}

data class GetDirectionsParameters(
    val currentLocation: GeolocationPoint,
    val destination: GeolocationPoint,
    val travelMode: TravelMode
)