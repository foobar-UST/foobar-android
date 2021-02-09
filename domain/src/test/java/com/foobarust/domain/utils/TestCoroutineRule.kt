package com.foobarust.domain.utils

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

class TestCoroutineRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        testDispatcher.cleanupTestCoroutines()
    }
}

fun TestCoroutineRule.runBlockingTest(block: suspend() -> Unit) {
    testDispatcher.runBlockingTest { block() }
}

fun TestCoroutineRule.coroutineScope(): CoroutineScope {
    return CoroutineScope(testDispatcher)
}