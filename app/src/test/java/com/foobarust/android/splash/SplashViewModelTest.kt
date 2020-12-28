package com.foobarust.android.splash


import android.app.NotificationManager
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.foobarust.android.R
import com.foobarust.android.main.MainActivity
import com.foobarust.android.tutorial.TutorialFragment
import com.foobarust.android.utils.TestCoroutineRule
import com.foobarust.android.utils.getOrAwaitValue
import com.foobarust.android.utils.observeForTesting
import com.foobarust.android.utils.runBlockingTest
import com.foobarust.domain.repositories.PreferencesRepository
import com.foobarust.domain.usecases.onboarding.GetOnboardingCompletedUseCase
import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by kevin on 9/2/20
 */

@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest {

    // Synchronous background executor for live data
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher
    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Test
    fun onboarding_completed_navigate_to_overview() = coroutineRule.runBlockingTest {
        val viewModel = createSplashViewModel(isOnboardingCompleted = true)

        delay(SPLASH_DELAY)

        viewModel.navigateToMain.observeForTesting {
            assertEquals(
                viewModel.navigateToMain.getOrAwaitValue(),
                MainActivity::class
            )
        }
    }

    @Test
    fun onboarding_not_completed_navigate_to_onboarding() = coroutineRule.runBlockingTest {
        val viewModel = createSplashViewModel(isOnboardingCompleted = false)

        delay(SPLASH_DELAY)

        viewModel.navigateToMain.observeForTesting {
            assertEquals(
                viewModel.navigateToMain.getOrAwaitValue(),
                TutorialFragment::class
            )
        }
    }


    private fun createSplashViewModel(isOnboardingCompleted: Boolean): SplashViewModel {
        return SplashViewModel(
            context = mock(Context::class.java).apply {
                `when`(getString(R.string.notification_channel_default_id))
                    .thenReturn("default_notification_channel")
                `when`(getString(R.string.notification_channel_default_name))
                    .thenReturn("Default")
            },
            notificationManager = mock(NotificationManager::class.java),
            getOnboardingCompletedUseCase = createGetOnboardingCompletedUseCase(isOnboardingCompleted)
        )
    }

    private fun createGetOnboardingCompletedUseCase(result: Boolean): GetOnboardingCompletedUseCase {
        return object : GetOnboardingCompletedUseCase(
            preferencesRepository = mock(PreferencesRepository::class.java),
            coroutineDispatcher = coroutineRule.testDispatcher
        ) {
            override suspend fun execute(parameters: Unit): Boolean = result
        }
    }
}