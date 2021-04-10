package com.foobarust.domain.usecases.auth

import com.foobarust.domain.utils.TestCoroutineRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/10/21
 */

class TestCountDownTimerUseCase {

    private lateinit var countDownTimerUseCase: CountDownTimerUseCase

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        countDownTimerUseCase = CountDownTimerUseCase(
            coroutineDispatcher = TestCoroutineDispatcher()
        )
    }

    @Test
    fun `timer active before delay`() = runBlockingTest {
        val delay = 100L
        val results = mutableListOf<Boolean>()

        val timerJob = launch {
            countDownTimerUseCase(delay).collect {
                results.add(it)
            }
        }

        delay(80L)

        timerJob.cancel()

        assert(results.size == 1 && results[0])
    }

    @Test
    fun `timer inactive after delay`() = runBlockingTest {
        val delay = 100L
        val results = mutableListOf<Pair<Boolean, Long>>()

        countDownTimerUseCase(delay).collect {
            results.add(Pair(it, System.currentTimeMillis()))
        }

        delay(delay + 10L)

        assert(
            results.size == 2 &&
            results[0].first && !results[1].first &&
            (results[1].second - results[0].second) >= delay
        )
    }
}