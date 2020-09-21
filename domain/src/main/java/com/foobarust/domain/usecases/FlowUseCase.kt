package com.foobarust.domain.usecases

import com.foobarust.domain.states.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

/**
 * Base usecase using flow pattern
 * P: Input parameters
 * R: Output data
 * Created by kevin on 8/9/20
 */
abstract class FlowUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {

    // execute() function call can now be omitted
    // loadDataUseCase.execute(parameters) is now equals to
    // loadDataUseCase(parameters)
    operator fun invoke(parameters: P): Flow<Resource<R>> = execute(parameters)
        .onStart { emit(Resource.Loading()) }
        .catch { e -> emit(Resource.Error(e.message)) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameters: P): Flow<Resource<R>>
}