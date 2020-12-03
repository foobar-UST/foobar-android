package com.foobarust.android.onboarding

import android.content.Context
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.main.MainActivity
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.onboarding.GetOnboardingCompletedUseCase
import com.foobarust.domain.usecases.onboarding.SaveOnboardingCompletedUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

private const val ERROR_ONBOARDING_COMPLETE = "Error completing onboarding."

class OnboardingViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context,
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val saveOnboardingCompletedUseCase: SaveOnboardingCompletedUseCase
) : BaseViewModel() {

    val onboardingProperties = listOf(
        OnboardingProperty(
            imageRes = R.drawable.undraw_online_groceries,
            title = context.getString(R.string.onboarding_browse_food_title),
            description = context.getString(R.string.onboarding_browse_food_description),
            showCompleteButton = false
        ),
        OnboardingProperty(
            imageRes = R.drawable.undraw_eating_together,
            title = context.getString(R.string.onboarding_group_orders_title),
            description = context.getString(R.string.onboarding_group_orders_description),
            showCompleteButton = false
        ),
        OnboardingProperty(
            imageRes = R.drawable.undraw_takeout_boxes,
            title = context.getString(R.string.onboarding_pick_up_delivery_title),
            description = context.getString(R.string.onboarding_pick_up_delivery_description),
            showCompleteButton = true
        )
    )

    private val _navigateToMain = SingleLiveEvent<KClass<*>>()
    val navigateToMain: LiveData<KClass<*>>
        get() = _navigateToMain

    fun onOnboardingCompleted() = viewModelScope.launch {
        saveOnboardingCompletedUseCase(true)

        when (getOnboardingCompletedUseCase(Unit)) {
            is Resource.Success -> _navigateToMain.value = MainActivity::class
            is Resource.Error -> showToastMessage(ERROR_ONBOARDING_COMPLETE)
            is Resource.Loading -> Unit
        }
    }
}

@Parcelize
data class OnboardingProperty(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String,
    val showCompleteButton: Boolean
) : Parcelable

