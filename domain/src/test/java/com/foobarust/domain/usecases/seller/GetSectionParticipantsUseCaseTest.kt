package com.foobarust.domain.usecases.seller

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeSellerRepositoryImpl
import com.foobarust.testshared.repositories.FakeUserRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/28/21
 */

class GetSectionParticipantsUseCaseTest {

    private lateinit var getSectionParticipantsUseCase: GetSectionParticipantsUseCase
    private lateinit var fakeUserRepositoryImpl: FakeUserRepositoryImpl
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()

        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()

        fakeUserRepositoryImpl = FakeUserRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserDetail = dependencyContainer.fakeUserDetail,
            hasCompletedTutorial = true
        )

        getSectionParticipantsUseCase = GetSectionParticipantsUseCase(
            userRepository = fakeUserRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )

        getSectionParticipantsUseCase = GetSectionParticipantsUseCase(
            userRepository = fakeUserRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get limited participants success`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setNetworkError(false)
        fakeSellerRepositoryImpl.setNetworkError(false)

        val fakeUserIds = List(10) { UUID.randomUUID().toString() }
        val params = GetSectionParticipantsParameters(
            userIds = fakeUserIds,
            displayUsersCount = 4
        )

        val results = getSectionParticipantsUseCase(params).toList()
        val lastResult = results.last()
        assert(
            lastResult is Resource.Success &&
            lastResult.data.size == 4
        )
    }

    @Test
    fun `test get all participants success`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setNetworkError(false)
        fakeSellerRepositoryImpl.setNetworkError(false)

        val fakeUserIds = List(10) { UUID.randomUUID().toString() }
        val params = GetSectionParticipantsParameters(
            userIds = fakeUserIds,
            displayUsersCount = fakeUserIds.size
        )

        val results = getSectionParticipantsUseCase(params).toList()
        val lastResult = results.last()
        assert(
            lastResult is Resource.Success &&
            lastResult.data.size == fakeUserIds.size
        )
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeUserRepositoryImpl.setNetworkError(true)
        fakeSellerRepositoryImpl.setNetworkError(true)

        val fakeUserIds = List(10) { UUID.randomUUID().toString() }
        val params = GetSectionParticipantsParameters(
            userIds = fakeUserIds,
            displayUsersCount = fakeUserIds.size
        )

        val results = getSectionParticipantsUseCase(params).toList()

        print(results.last())
        assert(results.last() is Resource.Error)
    }
}