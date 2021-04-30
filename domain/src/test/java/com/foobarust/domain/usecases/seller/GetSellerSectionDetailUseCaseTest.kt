package com.foobarust.domain.usecases.seller

import com.foobarust.domain.states.Resource
import com.foobarust.testshared.repositories.FakeSellerRepositoryImpl
import com.foobarust.testshared.serialize.toSellerSectionDetail
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kevin on 4/29/21
 */

class GetSellerSectionDetailUseCaseTest {

    private lateinit var getSellerSectionDetailUseCase: GetSellerSectionDetailUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        getSellerSectionDetailUseCase = GetSellerSectionDetailUseCase(
            sellerRepository = fakeSellerRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test get section detail success`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(false)

        val section = fakeSellerRepositoryImpl.sellerSectionList.random()
        val result = getSellerSectionDetailUseCase(section.id).toList().last()

        assert(
            result is Resource.Success &&
            result.data == section.toSellerSectionDetail()
        )
    }

    @Test
    fun `test network error`() = coroutineRule.runBlockingTest {
        fakeSellerRepositoryImpl.setNetworkError(true)

        val section = fakeSellerRepositoryImpl.sellerSectionList.random()
        val result = getSellerSectionDetailUseCase(section.id).toList().last()

        assert(result is Resource.Error)
    }
}