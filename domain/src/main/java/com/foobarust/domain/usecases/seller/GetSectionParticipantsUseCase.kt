package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 12/27/20
 */

class GetSectionParticipantsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetSectionParticipantsParameters, List<UserPublic>>(coroutineDispatcher) {

    override suspend fun execute(parameters: GetSectionParticipantsParameters): List<UserPublic> {
        return parameters.userIds
            .take(parameters.numOfUsers)
            .map { userId -> userRepository.getUserPublicInfo(userId = userId) }
    }
}

data class GetSectionParticipantsParameters(
    val userIds: List<String>,
    val numOfUsers: Int
)