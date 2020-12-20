package com.foobarust.android

import androidx.test.ext.junit.rules.activityScenarioRule
import com.foobarust.data.remoteapi.RemoteService
import com.foobarust.domain.repositories.AuthRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RemoteTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val activityScenarioRule = activityScenarioRule<InsertFakeDataActivity>()

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun test() = runBlocking(Dispatchers.IO) {


        assert(true)
    }
}