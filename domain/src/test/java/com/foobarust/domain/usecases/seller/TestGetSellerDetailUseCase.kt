package com.foobarust.domain.usecases.seller

/**
 * Created by kevin on 2/8/21
 */
/*
class TestGetSellerDetailUseCase {

    // System in test
    private lateinit var getSellerDetailUseCase: GetSellerDetailUseCase

    // Dependencies
    private lateinit var sellerRepository: FakeSellerRepositoryImpl

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        sellerRepository = FakeSellerRepositoryImpl()
        getSellerDetailUseCase = GetSellerDetailUseCase(
            sellerRepository = sellerRepository,
            coroutineDispatcher = TestCoroutineDispatcher()
        )
    }

    @Test
    fun `get seller detail return success`() = coroutineRule.runBlockingTest {
        val sellerDetail = provideSellerDetail()

        sellerRepository.removeAllSellerDetail()
        sellerRepository.addSellerDetail(sellerDetail)
        sellerRepository.setShouldReturnNetworkError(false)

        val result = getSellerDetailUseCase(sellerDetail.id).toList()

        assert(result.last() == Resource.Success(sellerDetail))
    }

    @Test
    fun `get seller detail return not found`() = coroutineRule.runBlockingTest {
        val sellerId = UUID.randomUUID().toString()

        sellerRepository.removeAllSellerDetail()
        sellerRepository.setShouldReturnNetworkError(false)

        val result = getSellerDetailUseCase(sellerId).toList()

        assert(result.last() is Resource.Error)
    }

    @Test
    fun `get seller detail return network error`() = coroutineRule.runBlockingTest {
        val sellerDetail = provideSellerDetail()

        sellerRepository.removeAllSellerDetail()
        sellerRepository.addSellerDetail(sellerDetail)
        sellerRepository.setShouldReturnNetworkError(true)

        val result = getSellerDetailUseCase(sellerDetail.id).toList()

        assert(result.last() is Resource.Error)
    }

    private fun provideSellerDetail(): SellerDetail {
        val sellerId = UUID.randomUUID().toString()
        return SellerDetail(
            id = sellerId, name = "Happy Restaurant", nameZh = "餐廳", description = "description",
            descriptionZh = "介紹", phoneNum = "+852 1234-5678", website = "about:blank",
            location = Geolocation(
                address = "abc road", addressZh = "地址", locationPoint =
                GeolocationPoint(latitude = 1.0, longitude = 2.0)
            ),
            imageUrl = "about:blank", minSpend = 10.0, orderRating = 4.9, ratingCount = 100,
            type = SellerType.ON_CAMPUS, online = true, notice = "notice",
            openingHours = "MonTueWed 15:00", tags = listOf("Chinese", "Western")
        )
    }
}

 */