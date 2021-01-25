package com.foobarust.domain.usecases.maps

import com.foobarust.domain.di.MainDispatcher
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.repositories.MapRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/20/21
 */

class GetStaticMapImageUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @MainDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Geolocation, String>(coroutineDispatcher) {

    override fun execute(parameters: Geolocation): Flow<Resource<String>> = flow {
        val imageUrl = mapRepository.getStaticMapImageUrl(
            latitude = parameters.locationPoint.latitude,
            longitude = parameters.locationPoint.longitude
        )
        emit(Resource.Success(imageUrl))
    }
}
