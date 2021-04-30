package com.foobarust.android.seller

import android.content.Context
import app.cash.turbine.test
import com.foobarust.android.di.RepositoryContainer
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/30/21
 */

class SellerViewModelTest {

    private lateinit var repositoryContainer: RepositoryContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        repositoryContainer = RepositoryContainer(coroutineRule.coroutineScope())
    }

    @Test
    fun `test navigate to seller detail`() = coroutineRule.runBlockingTest {
        val sellerViewModel = createSellerViewModel()
        val sellerId = UUID.randomUUID().toString()

        sellerViewModel.navigateToSellerDetail.test {
            sellerViewModel.onNavigateToSellerDetail(sellerId)
            assertEquals(expectItem(), SellerDetailProperty(sellerId))
        }
    }

    @Test
    fun `test navigate to seller section`() = coroutineRule.runBlockingTest {
        val sellerViewModel = createSellerViewModel()
        val sectionId = UUID.randomUUID().toString()

        sellerViewModel.navigateToSellerSection.test {
            sellerViewModel.onNavigateToSellerSection(sectionId)
            assertEquals(expectItem(), sectionId)
        }
    }

    @Test
    fun `test navigate to promotion detail`() = coroutineRule.runBlockingTest {
        val sellerViewModel = createSellerViewModel()
        val promotionUrl = "about:blank"

        sellerViewModel.navigateToPromotionDetail.test {
            sellerViewModel.onNavigateToPromotionDetail(promotionUrl)
            assertEquals(expectItem(), promotionUrl)
        }
    }

    @Test
    fun `test page scroll to top`() = coroutineRule.runBlockingTest {
        val sellerViewModel = createSellerViewModel()
        val currentPageTag = sellerViewModel.sellerPages.first().tag

        sellerViewModel.pageScrollToTop.test {
            sellerViewModel.onCurrentPageChanged(currentPageTag)
            sellerViewModel.onPageScrollToTop()
            assertEquals(expectItem(), currentPageTag)
        }
    }

    private fun createSellerViewModel(): SellerViewModel = SellerViewModel(
        context = mockApplicationContext()
    )

    private fun mockApplicationContext(): Context {
        return mockk {
            every { getString(any()) } returns "mocked string"
        }
    }
}