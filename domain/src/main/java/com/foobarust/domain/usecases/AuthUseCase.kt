package com.foobarust.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

abstract class AuthUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(parameters: P): Flow<AuthState<R>> = execute(parameters)
        .onStart { emit(AuthState.Loading) }
        .catch { emit(AuthState.Unauthenticated) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameters: P): Flow<AuthState<R>>
}

sealed class AuthState<out T> {
    data class Authenticated<out T>(val data: T) : AuthState<T>()
    object Unauthenticated : AuthState<Nothing>()
    object Loading : AuthState<Nothing>()
}
