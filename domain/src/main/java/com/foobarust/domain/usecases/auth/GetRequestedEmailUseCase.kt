package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by kevin on 8/28/20
 */

private const val ERROR_NO_EMAIL_TO_VERIFY = "No saved email to be verified."

class GetRequestedEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, String>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<String>> {
        return authRepository.getAuthRequestedEmail().map {
            when (it) {
                is Resource.Success -> {
                    if (it.data == null) {
                        Resource.Error(ERROR_NO_EMAIL_TO_VERIFY)
                    } else {
                        Resource.Success(it.data)
                    }
                }
                is Resource.Error -> Resource.Error(it.message)
                is Resource.Loading -> Resource.Loading()
            }
        }
    }
}