package com.foobarust.android

import android.util.Log
import androidx.test.ext.junit.rules.activityScenarioRule
import com.foobarust.data.api.RemoteService
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.MapRepository
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
    lateinit var mapRepository: MapRepository

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun init() { hiltRule.inject() }

    @Test
    fun hello_world_test() = runBlocking(Dispatchers.IO) {
        try {
            val result = remoteService.getHelloWorld(hasError = true)
            Log.d("RemoteTest", "result: $result")
        } catch (e: Exception) {
            Log.d("RemoteTest", "message: ${e.message}")
        }

        assert(true)
    }

    @Test
    fun get_directions_test() = runBlocking(Dispatchers.IO) {
        val result = mapRepository.getDirectionsPath(
            originLatitude = 22.33469,
            originLongitude = 114.20854,
            destLatitude = 22.337517,
            destLongitude = 114.263587
        )
        Log.d("RemoteTest", "$result")
        assert(true)
    }
}