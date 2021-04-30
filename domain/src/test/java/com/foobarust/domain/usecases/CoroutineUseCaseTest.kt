package com.foobarust.domain.usecases

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.CoroutineDispatcher
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/21/21
 */

class CoroutineUseCaseTest {

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Test
    fun `test success resource`() = coroutineRule.runBlockingTest {
        val exampleCoroutineUseCase = ExampleCoroutineUseCase(coroutineRule.testDispatcher)
        val result = exampleCoroutineUseCase(Unit)
        assert(result is Resource.Success)
    }

    @Test
    fun `test error resource`() = coroutineRule.runBlockingTest {
        val exampleCoroutineUseCase = ExampleCoroutineUseCase(coroutineRule.testDispatcher, true)
        val result = exampleCoroutineUseCase(Unit)
        assert(result is Resource.Error)
    }

    private class ExampleCoroutineUseCase(
        coroutineDispatcher: CoroutineDispatcher,
        private val throwError: Boolean = false
    ) : CoroutineUseCase<Unit, Int>(coroutineDispatcher) {

        override suspend fun execute(parameters: Unit): Int {
            return if (throwError) {
                throw Exception("use case error.")
            } else {
                0
            }
        }
    }
}