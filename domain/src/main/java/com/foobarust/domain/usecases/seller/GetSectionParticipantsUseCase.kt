package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 12/27/20
 */

class GetSectionParticipantsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetSectionParticipantsParameters, List<UserPublic>>(coroutineDispatcher) {

    override fun execute(
        parameters: GetSectionParticipantsParameters
    ): Flow<Resource<List<UserPublic>>> = flow {
        val usersPublicInfo = parameters.userIds
            .take(parameters.numOfUsers)
            .map { userId -> userRepository.getUserPublicInfo(userId = userId) }

        emit(Resource.Success(usersPublicInfo))
    }
}

data class GetSectionParticipantsParameters(
    val userIds: List<String>,
    val numOfUsers: Int
)