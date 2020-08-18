package com.foobarust.domain.interactors

import com.foobarust.domain.states.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

/**
 * Created by kevin on 8/9/20
 * Base use case using coroutine flow pattern
 * P: Input parameters
 * R: Output data
 */
abstract class FlowUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {

    // Override "()" invoke operator to invoke the execution immediately,
    // so that execute function call can be omitted
    // loadDataUseCase.execute(parameters) is now equals to loadDataUseCase(parameters)
    operator fun invoke(parameters: P): Flow<Result<R>> = execute(parameters)
        .onStart { emit(Result.Loading) }
        .catch { e -> emit(Result.Error(e.message)) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameters: P): Flow<Result<R>>
}