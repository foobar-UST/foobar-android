package com.foobarust.domain.usecases.auth

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.repository.FakeAuthRepositoryImpl
import com.foobarust.domain.states.Resource
import com.foobarust.domain.utils.TestCoroutineRule
import com.foobarust.domain.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/21/21
 */

class TestGetSavedAuthEmailUseCase {

    private lateinit var getSavedAuthEmailUseCase: GetSavedAuthEmailUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
            idToken = UUID.randomUUID().toString(),
            defaultAuthProfile = AuthProfile(
                id = UUID.randomUUID().toString(),
                email = FAKE_AUTH_EMAIL,
                username = "hello_world"
            ),
            isSignedIn = false
        )

        getSavedAuthEmailUseCase = GetSavedAuthEmailUseCase(
            authRepository = fakeAuthRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get saved auth email success`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(false)
        fakeAuthRepositoryImpl.updateSavedAuthEmail(FAKE_AUTH_EMAIL)
        val result = getSavedAuthEmailUseCase(Unit).toList().last()
        assert(result is Resource.Success && result.data == FAKE_AUTH_EMAIL)
    }

    @Test
    fun `test get saved auth email failed`() = coroutineRule.runBlockingTest {
        fakeAuthRepositoryImpl.setIOError(true)
        val result = getSavedAuthEmailUseCase(Unit).toList().last()
        assert(result is Resource.Error)
    }

    companion object {
        private const val FAKE_AUTH_EMAIL = "test@test.com"
    }
}