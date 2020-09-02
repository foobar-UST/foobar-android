package com.foobarust.android.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Test rule that overrides main dispatcher with test dispatcher
 */

class MainCoroutineRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        testDispatcher.cleanupTestCoroutines()
    }
}

fun MainCoroutineRule.runBlockingTest(block: suspend() -> Unit) {
    testDispatcher.runBlockingTest { block() }
}

fun MainCoroutineRule.Coroutinescope(): CoroutineScope {
    return CoroutineScope(testDispatcher)
}