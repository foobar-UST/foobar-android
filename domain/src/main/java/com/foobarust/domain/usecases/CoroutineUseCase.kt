package com.foobarust.domain.usecases

import com.foobarust.domain.states.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Created by kevin on 8/26/20
 */

abstract class CoroutineUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(parameters: P): Resource<R> {
        return try {
            // Moving all use case's executions to the injected dispatcher
            // In production code, this is usually the Default dispatcher (background thread)
            // In tests, this becomes a TestCoroutineDispatcher
            withContext(coroutineDispatcher) {
                execute(parameters).let {
                    Resource.Success(it)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}