package com.foobarust.android.splash


import android.app.NotificationManager
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.foobarust.android.R
import com.foobarust.android.overview.OverviewActivity
import com.foobarust.android.signin.SignInActivity
import com.foobarust.android.utils.MainCoroutineRule
import com.foobarust.android.utils.getOrAwaitValue
import com.foobarust.android.utils.observeForTesting
import com.foobarust.android.utils.runBlockingTest
import com.foobarust.domain.repositories.PreferencesRepository
import com.foobarust.domain.usecases.auth.GetSkippedSignInUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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
    var coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth


    @Test
    fun skip_signin_navigate_to_overview() = coroutineRule.runBlockingTest {
        val viewModel = createSplashViewModel(firebaseAuth, isSkippedSignIn = true)

        delay(SPLASH_DELAY)

        viewModel.startNavigation.observeForTesting {
            assertEquals(
                viewModel.startNavigation.getOrAwaitValue(),
                OverviewActivity::class
            )
        }
    }

    @Test
    fun not_skip_signin_navigate_to_signin() = coroutineRule.runBlockingTest {
        val viewModel = createSplashViewModel(isSkippedSignIn = false)

        delay(SPLASH_DELAY)

        viewModel.startNavigation.observeForTesting {
            assertEquals(
                viewModel.startNavigation.getOrAwaitValue(),
                SignInActivity::class
            )
        }
    }

    @Test
    fun not_signed_in_navigate_to_signin() = coroutineRule.runBlockingTest {
        // Mock a unsigned in user
        val firebaseAuth = mock(FirebaseAuth::class.java).apply {
            `when`(currentUser).thenReturn(null)
        }

        val viewModel = createSplashViewModel(
            firebaseAuth = firebaseAuth,
            isSkippedSignIn = false
        )

        delay(SPLASH_DELAY)

        viewModel.startNavigation.observeForTesting {
            assertEquals(
                viewModel.startNavigation.getOrAwaitValue(),
                SignInActivity::class
            )
        }
    }

    @Test
    fun signed_in_navigate_to_overview() = coroutineRule.runBlockingTest {
        val firebaseAuth = mock(FirebaseAuth::class.java).apply {
            val user = mock(FirebaseUser::class.java)
            `when`(currentUser).thenReturn(user)
        }
        val viewModel = createSplashViewModel(
            firebaseAuth = firebaseAuth,
            isSkippedSignIn = false
        )

        delay(SPLASH_DELAY)

        viewModel.startNavigation.observeForTesting {
            assertEquals(
                viewModel.startNavigation.getOrAwaitValue(),
                OverviewActivity::class
            )
        }
    }

    private fun createSplashViewModel(
        firebaseAuth: FirebaseAuth = mock(FirebaseAuth::class.java),
        isSkippedSignIn: Boolean
    ): SplashViewModel {
        return SplashViewModel(
            context = mock(Context::class.java).apply {
                `when`(getString(R.string.foobar_default_notification_channel_id))
                    .thenReturn("default_notification_channel")
                `when`(getString(R.string.foobar_default_notification_channel_name))
                    .thenReturn("Default")
            },
            notificationManager = mock(NotificationManager::class.java),
            firebaseAuth = firebaseAuth,
            getSkippedSignInUseCase = createGetSkippedSignInUseCase(isSkippedSignIn)
        )
    }

    private fun createGetSkippedSignInUseCase(result: Boolean): GetSkippedSignInUseCase {
        return object : GetSkippedSignInUseCase(
            preferencesRepository = mock(PreferencesRepository::class.java),
            coroutineDispatcher = coroutineRule.testDispatcher
        ) {
            override suspend fun execute(parameters: Unit): Boolean = result
        }
    }
}