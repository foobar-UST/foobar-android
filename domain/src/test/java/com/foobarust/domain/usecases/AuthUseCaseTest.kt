package com.foobarust.domain.usecases

import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/21/21
 */

class AuthUseCaseTest {

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Test
    fun `test success, user authenticated`() = coroutineRule.runBlockingTest {
        val exampleAuthUseCase = ExampleAuthUseCase(coroutineRule.testDispatcher)
        val results = exampleAuthUseCase(Unit).toList()
        assert(results[0] is AuthState.Loading)
        assert(results[1] is AuthState.Authenticated)
    }

    @Test
    fun `test error, user unauthenticated`() = coroutineRule.runBlockingTest {
        val exampleAuthUseCase = ExampleAuthUseCase(coroutineRule.testDispatcher, true)
        val results = exampleAuthUseCase(Unit).toList()
        assert(results[0] is AuthState.Loading)
        assert(results[1] is AuthState.Unauthenticated)
    }

    private class ExampleAuthUseCase(
        coroutineDispatcher: CoroutineDispatcher,
        private val throwError: Boolean = false
    ) : AuthUseCase<Unit, Unit>(coroutineDispatcher) {

        override fun execute(parameters: Unit): Flow<AuthState<Unit>> = flow {
            if (throwError) {
                throw Exception("auth error.")
            } else {
                emit(AuthState.Authenticated(Unit))
            }
        }
    }
}