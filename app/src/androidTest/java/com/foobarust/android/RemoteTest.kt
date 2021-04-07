package com.foobarust.android

import androidx.test.ext.junit.rules.activityScenarioRule
import com.foobarust.data.api.RemoteService
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.CheckoutRepository
import com.foobarust.domain.repositories.MapRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
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
    lateinit var checkoutRepository: CheckoutRepository

    @Inject
    lateinit var mapRepository: MapRepository

    @Inject
    lateinit var remoteService: RemoteService

    @Before
    fun init() {
        hiltRule.inject()
    }
}