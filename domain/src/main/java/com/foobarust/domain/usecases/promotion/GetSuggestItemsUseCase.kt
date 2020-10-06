package com.foobarust.domain.usecases.promotion

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.SuggestBasic
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.PromotionRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 10/3/20
 */

private const val GET_SUGGEST_ITEMS_NOT_SIGNED_IN = "User is not signed in."

class GetSuggestItemsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val promotionRepository: PromotionRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<SuggestBasic>>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<List<SuggestBasic>>> = flow {
        // Check if user is signed in
        if (!authRepository.isSignedIn()) {
            throw Exception(GET_SUGGEST_ITEMS_NOT_SIGNED_IN)
        }

        val userId = authRepository.getAuthUserId()

        emitAll(promotionRepository.getSuggestItems(userId))
    }
}