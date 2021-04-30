package com.foobarust.android.sellerdetail

import android.content.Context
import androidx.viewpager2.widget.ViewPager2
import app.cash.turbine.test
import com.foobarust.android.di.RepositoryContainer
import com.foobarust.android.selleritem.SellerItemDetailProperty
import com.foobarust.android.utils.AppBarLayoutState
import com.foobarust.domain.models.seller.getNormalizedName
import com.foobarust.domain.usecases.seller.GetSellerDetailWithCatalogsUseCase
import com.foobarust.domain.usecases.seller.GetSellerSectionDetailUseCase
import com.foobarust.testshared.serialize.toSellerCatalog
import com.foobarust.testshared.serialize.toSellerDetail
import com.foobarust.testshared.serialize.toSellerSectionDetail
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
 * Created by kevin on 5/1/21
 */

class SellerDetailViewModelTest {

    private lateinit var repositoryContainer: RepositoryContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        repositoryContainer = RepositoryContainer(coroutineRule.coroutineScope())
    }

    @Test
    fun `test load seller detail`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()
        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()

        sellerDetailViewModel.onFetchSellerDetail(
            property = SellerDetailProperty(sellerDetail.id)
        )

        sellerDetailViewModel.sellerDetail.test {
            assertEquals(expectItem(), sellerDetail)
        }
    }

    @Test
    fun `test load seller catalogs`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()

        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()

        val sellerCatalogs = repositoryContainer.sellerRepository.sellerCatalogList
            .filter { it.seller_id == sellerDetail.id }
            .map { it.toSellerCatalog() }

        sellerDetailViewModel.onFetchSellerDetail(
            property = SellerDetailProperty(sellerDetail.id)
        )

        sellerDetailViewModel.sellerCatalogs.test {
            val currentTime = Date()
            val results = expectItem().map { it.copy(updatedAt = currentTime) }
            val expected = sellerCatalogs.map { it.copy(updatedAt = currentTime) }

            assertEquals(results, expected)
        }
    }

    @Test
    fun `test load section detail`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()
        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()
        val sectionDetail = repositoryContainer.sellerRepository.sellerSectionList.random()
            .toSellerSectionDetail()

        sellerDetailViewModel.onFetchSellerDetail(
            property = SellerDetailProperty(sellerDetail.id, sectionDetail.id)
        )

        sellerDetailViewModel.sectionDetail.test {
            assertEquals(expectItem(), sectionDetail)
        }
    }

    @Test
    fun `test ui states when load success`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()
        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()

        sellerDetailViewModel.sellerDetailUiState.test {
            assert(expectItem() is SellerDetailUiState.Loading)

            sellerDetailViewModel.onFetchSellerDetail(
                property = SellerDetailProperty(sellerDetail.id)
            )
            assert(expectItem() is SellerDetailUiState.Success)
        }
    }

    @Test
    fun `test ui states when load error`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel(hasNetworkError = true)
        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()

        sellerDetailViewModel.sellerDetailUiState.test {
            assert(expectItem() is SellerDetailUiState.Loading)

            sellerDetailViewModel.onFetchSellerDetail(
                property = SellerDetailProperty(sellerDetail.id)
            )
            assert(expectItem() is SellerDetailUiState.Error)
        }
    }

    @Test
    fun `test navigate to item detail`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()

        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()
        val itemId = repositoryContainer.sellerRepository.sellerItemList.first {
            it.seller_id == sellerDetail.id
        }.id

        sellerDetailViewModel.navigateToItemDetail.test {
            sellerDetailViewModel.onFetchSellerDetail(
                property = SellerDetailProperty(sellerDetail.id)
            )
            sellerDetailViewModel.onNavigateToSellerItemDetail(itemId)

            assertEquals(
                expectItem(),
                SellerItemDetailProperty(
                    sellerId = sellerDetail.id,
                    itemId = itemId,
                    sectionId = sellerDetailViewModel.sectionDetail.value?.id
                )
            )
        }
    }

    @Test
    fun `test finish swipe refresh after load`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()
        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()

        sellerDetailViewModel.finishSwipeRefresh.test {
            sellerDetailViewModel.onFetchSellerDetail(
                property = SellerDetailProperty(sellerDetail.id),
                isSwipeRefresh = true
            )

            assertEquals(expectItem(), Unit)
        }
    }

    @Test
    fun `test toolbar idle or expanded, show no title`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()

        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()

        sellerDetailViewModel.onFetchSellerDetail(
            property = SellerDetailProperty(sellerDetail.id),
        )

        sellerDetailViewModel.toolbarTitle.test {
            sellerDetailViewModel.onAppBarLayoutStateChanged(AppBarLayoutState.IDLE)
            assertEquals(expectItem(), null)

            sellerDetailViewModel.onAppBarLayoutStateChanged(AppBarLayoutState.EXPANDED)
            assertEquals(expectItem(), null)
        }
    }

    @Test
    fun `test toolbar collapsed, show title`() = coroutineRule.runBlockingTest {
        val sellerDetailViewModel = createSellerDetailViewModel()
        val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
            .toSellerDetail()

        sellerDetailViewModel.onAppBarLayoutStateChanged(AppBarLayoutState.COLLAPSED)

        sellerDetailViewModel.onFetchSellerDetail(
            property = SellerDetailProperty(sellerDetail.id),
        )

        sellerDetailViewModel.toolbarTitle.test {
            val expected = sellerDetailViewModel.sellerDetail.value?.getNormalizedName()
            assertEquals(expectItem(), expected)
        }
    }

    @Test
    fun `test enable swipe refresh, when toolbar is expanded and view pager is idle`() =
        coroutineRule.runBlockingTest {
            val sellerDetailViewModel = createSellerDetailViewModel()
            val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
                .toSellerDetail()

            sellerDetailViewModel.onFetchSellerDetail(
                property = SellerDetailProperty(sellerDetail.id)
            )

            sellerDetailViewModel.enableSwipeRefresh.test {
                sellerDetailViewModel.onAppBarLayoutStateChanged(AppBarLayoutState.EXPANDED)
                assertEquals(expectItem(), false)

                sellerDetailViewModel.onViewPagerScrollStateChanged(ViewPager2.SCROLL_STATE_IDLE)
                assertEquals(expectItem(), true)
            }
        }

    @Test
    fun `test enable swipe refresh, when toolbar is collapsed`() =
        coroutineRule.runBlockingTest {
            val sellerDetailViewModel = createSellerDetailViewModel()
            val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
                .toSellerDetail()

            sellerDetailViewModel.onFetchSellerDetail(
                property = SellerDetailProperty(sellerDetail.id)
            )

            sellerDetailViewModel.enableSwipeRefresh.test {
                assertEquals(expectItem(), false)

                sellerDetailViewModel.onAppBarLayoutStateChanged(AppBarLayoutState.COLLAPSED)
                assertEquals(expectItem(), false)
            }
        }

    @Test
    fun `test enable swipe refresh, when viewpager is dragging`() =
        coroutineRule.runBlockingTest {
            val sellerDetailViewModel = createSellerDetailViewModel()
            val sellerDetail = repositoryContainer.sellerRepository.sellerList.random()
                .toSellerDetail()

            sellerDetailViewModel.onFetchSellerDetail(
                property = SellerDetailProperty(sellerDetail.id)
            )

            sellerDetailViewModel.enableSwipeRefresh.test {
                assertEquals(expectItem(), false)

                sellerDetailViewModel.onViewPagerScrollStateChanged(ViewPager2.SCROLL_STATE_DRAGGING)
                assertEquals(expectItem(), false)
            }
        }

    private fun createSellerDetailViewModel(
        hasNetworkError: Boolean = false
    ): SellerDetailViewModel {
        repositoryContainer.setNetworkError(hasNetworkError)
        return SellerDetailViewModel(
            context = mockApplicationContext(),
            getSellerDetailWithCatalogsUseCase = GetSellerDetailWithCatalogsUseCase(
                sellerRepository = repositoryContainer.sellerRepository,
                coroutineDispatcher = coroutineRule.testDispatcher
            ),
            getSellerSectionDetailUseCase = GetSellerSectionDetailUseCase(
                sellerRepository = repositoryContainer.sellerRepository,
                coroutineDispatcher = coroutineRule.testDispatcher
            )
        )
    }

    private fun mockApplicationContext(): Context = mockk {
        every { getString(any()) } returns "mocked string"
    }
}