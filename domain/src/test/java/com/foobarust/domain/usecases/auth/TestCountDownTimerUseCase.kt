package com.foobarust.domain.usecases.auth

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by kevin on 4/10/21
 */

class TestCountDownTimerUseCase {

    private lateinit var countDownTimerUseCase: CountDownTimerUseCase

    @Before
    fun init() {
        countDownTimerUseCase = CountDownTimerUseCase(
            coroutineDispatcher = TestCoroutineDispatcher()
        )
    }

    @Test
    fun `timer is active when started`() = runBlocking {
        val results = countDownTimerUseCase(100L).toList()
        assertTrue(results.first())
    }

    @Test
    fun `timer is inactive when ended`() = runBlocking {
        val results = countDownTimerUseCase(100L).toList()
        assertFalse(results.last())
    }
}