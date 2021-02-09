package com.foobarust.android.tutorial

import android.content.Context
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.onboarding.GetUserCompleteTutorialUseCase
import com.foobarust.domain.usecases.onboarding.UpdateUserCompleteTutorialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getUserCompleteTutorialUseCase: GetUserCompleteTutorialUseCase,
    private val updateUserCompleteTutorialUseCase: UpdateUserCompleteTutorialUseCase
) : ViewModel() {

    val tutorialProperties = listOf(
        TutorialProperty(
            title = context.getString(R.string.onboarding_browse_food_title),
            description = context.getString(R.string.onboarding_browse_food_description),
            drawableRes = R.drawable.undraw_online_groceries,
        ),
        TutorialProperty(
            title = context.getString(R.string.onboarding_group_orders_title),
            description = context.getString(R.string.onboarding_group_orders_description),
            drawableRes = R.drawable.undraw_eating_together,
        ),
        TutorialProperty(
            title = context.getString(R.string.onboarding_pick_up_delivery_title),
            description = context.getString(R.string.onboarding_pick_up_delivery_description),
            drawableRes = R.drawable.undraw_takeout_boxes,
            showDismiss = true
        )
    )

    private val _dismissTutorial = SingleLiveEvent<Unit>()
    val dismissTutorial: LiveData<Unit>
        get() = _dismissTutorial

    fun onCompleteTutorial() = viewModelScope.launch {
        val completed = getUserCompleteTutorialUseCase(Unit).getSuccessDataOr(false)
        if (!completed) {
            updateUserCompleteTutorialUseCase(true)
        }
        _dismissTutorial.value = Unit
    }
}

@Parcelize
data class TutorialProperty(
    val title: String,
    val description: String,
    @DrawableRes val drawableRes: Int,
    val showDismiss: Boolean = false
) : Parcelable

