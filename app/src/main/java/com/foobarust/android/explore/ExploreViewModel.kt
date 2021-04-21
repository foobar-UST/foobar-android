package com.foobarust.android.explore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.explore.ExploreListModel.ExploreItemCategoryItemModel
import com.foobarust.android.explore.ExploreListModel.ExploreSubtitleItemModel
import com.foobarust.android.seller.SellerListProperty
import com.foobarust.domain.models.explore.ItemCategory
import com.foobarust.domain.models.explore.getNormalizedTitle
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerItemCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/14/21
 */

@HiltViewModel
class ExploreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getSellerItemCategoriesUseCase: GetSellerItemCategoriesUseCase
) : ViewModel() {

    private val _exploreUiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val exploreUiState: StateFlow<ExploreUiState> = _exploreUiState.asStateFlow()

    private val _exploreListModels = MutableStateFlow<List<ExploreListModel>>(emptyList())
    val exploreListModels: StateFlow<List<ExploreListModel>> = _exploreListModels.asStateFlow()

    private val _refreshExploreList = ConflatedBroadcastChannel(Unit)

    private val _navigateToSellerList = Channel<SellerListProperty>()
    val navigateToSellerList: Flow<SellerListProperty> = _navigateToSellerList.receiveAsFlow()

    init {
        viewModelScope.launch {
            _refreshExploreList.asFlow()
                .flatMapLatest { getSellerItemCategoriesUseCase(Unit) }
                .collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            _exploreUiState.value = ExploreUiState.Success
                            _exploreListModels.value = buildExploreListModels(it.data)
                        }
                        is Resource.Error -> {
                            _exploreUiState.value = ExploreUiState.Error(it.message)
                        }
                        is Resource.Loading -> {
                            _exploreUiState.value = ExploreUiState.Loading
                        }
                    }
                }
        }
    }

    fun onRefreshExploreList() {
        _refreshExploreList.offer(Unit)
    }

    fun onNavigateToSellerList(categoryId: String) {
        val categoryItemModel = _exploreListModels.value
            .filterIsInstance<ExploreItemCategoryItemModel>()
            .first { it.categoryId == categoryId }

        _navigateToSellerList.offer(
            SellerListProperty(
                categoryTag = categoryItemModel.categoryTag,
                categoryTitle = categoryItemModel.categoryTitle,
                categoryImageUrl = categoryItemModel.categoryImageUrl
            )
        )
    }

    private fun buildExploreListModels(
        itemCategories: List<ItemCategory>
    ): List<ExploreListModel> = buildList {
        if (itemCategories.isNotEmpty()) {
            add(ExploreSubtitleItemModel)
        }

        addAll(itemCategories.map { itemCategory ->
            ExploreItemCategoryItemModel(
                categoryId = itemCategory.id,
                categoryTag = itemCategory.tag,
                categoryTitle = itemCategory.getNormalizedTitle(),
                categoryImageUrl = itemCategory.imageUrl
            )
        })
    }
}

sealed class ExploreUiState {
    object Success : ExploreUiState()
    data class Error(val message: String?) : ExploreUiState()
    object Loading : ExploreUiState()
}