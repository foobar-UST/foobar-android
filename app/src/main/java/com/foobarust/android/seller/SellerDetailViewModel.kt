package com.foobarust.android.seller

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.domain.models.SellerDetail
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.seller.GetSellerDetailUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/4/20
 */

class SellerDetailViewModel @ViewModelInject constructor(
    private val getSellerDetailUseCase: GetSellerDetailUseCase
) : BaseViewModel() {

    private val _sellerDetail = MutableLiveData<SellerDetail>()
    val sellerDetail: LiveData<SellerDetail>
        get() = _sellerDetail

    private val _showToolbarTitle = MutableLiveData<Boolean>()
    val showToolbarTitle: LiveData<Boolean>
        get() = _showToolbarTitle

    fun onFetchSellerDetail(sellerId: String) = viewModelScope.launch {
        getSellerDetailUseCase(sellerId).collect {
            controlLoadingProgress(isShow = it is Resource.Loading)

            when (it) {
                is Resource.Success -> _sellerDetail.value = it.data
                is Resource.Error -> showMessage(it.message)
            }
        }
    }

    fun onShowToolbarTitleChanged(isShow: Boolean) {
        _showToolbarTitle.value = isShow
    }
}