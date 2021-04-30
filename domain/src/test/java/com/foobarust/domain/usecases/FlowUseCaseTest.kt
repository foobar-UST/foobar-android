package com.foobarust.domain.usecases

import com.foobarust.domain.states.Resource
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

class FlowUseCaseTest {

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Test
    fun `test success resource`() = coroutineRule.runBlockingTest {
        val exampleFlowUseCase = ExampleFlowUseCase(coroutineRule.testDispatcher)
        val results = exampleFlowUseCase(Unit).toList()
        assert(results[0] is Resource.Loading)
        assert(results[1] is Resource.Success)
    }

    @Test
    fun `test error resource`() = coroutineRule.runBlockingTest {
        val exampleFlowUseCase = ExampleFlowUseCase(coroutineRule.testDispatcher, true)
        val results = exampleFlowUseCase(Unit).toList()
        assert(results[0] is Resource.Loading)
        assert(results[1] is Resource.Error)
    }

    private class ExampleFlowUseCase(
        coroutineDispatcher: CoroutineDispatcher,
        private val throwError: Boolean = false
    ) : FlowUseCase<Unit, Unit>(coroutineDispatcher) {

        override fun execute(parameters: Unit): Flow<Resource<Unit>> = flow {
            if (throwError) {
                throw Exception("auth error.")
            } else {
                emit(Resource.Success(Unit))
            }
        }
    }
}