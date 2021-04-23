package com.foobarust.domain.usecases.auth

import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.launch
import com.foobarust.domain.utils.runBlockingTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/10/21
 */

class OneShotTimerUseCaseTest {

    private lateinit var oneShotTimerUseCase: OneShotTimerUseCase

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        oneShotTimerUseCase = OneShotTimerUseCase(
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `timer is active when started`() = coroutineRule.runBlockingTest {
        val results = oneShotTimerUseCase(100L).toList()
        assertTrue(results[0])
    }

    @Test
    fun `timer is active before elapsed time`() = coroutineRule.runBlockingTest {
        val delay = 100L
        val results = mutableListOf<Boolean>()
        val job = coroutineRule.launch {
            oneShotTimerUseCase(delay).collect { results.add(it) }
        }
        delay(delay / 2)
        assert(results.size == 1 && results[0])
        job.cancel()
    }

    @Test
    fun `timer is inactive when ended`() = coroutineRule.runBlockingTest {
        val delay = 100L
        val results = mutableListOf<Boolean>()
        val job = coroutineRule.launch {
            oneShotTimerUseCase(delay).collect { results.add(it) }
        }
        delay(delay + 10)
        assert(results.size == 2 && !results[1])
        job.cancel()

    }
}