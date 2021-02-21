package com.foobarust.android.tutorial

import android.content.Context
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.onboarding.GetUserCompleteTutorialUseCase
import com.foobarust.domain.usecases.onboarding.UpdateUserCompleteTutorialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getUserCompleteTutorialUseCase: GetUserCompleteTutorialUseCase,
    private val updateUserCompleteTutorialUseCase: UpdateUserCompleteTutorialUseCase
) : ViewModel() {

    val tutorialPageProperties = listOf(
        TutorialPage(
            title = context.getString(R.string.onboarding_browse_food_title),
            description = context.getString(R.string.onboarding_browse_food_description),
            drawableRes = R.drawable.undraw_online_groceries,
        ),
        TutorialPage(
            title = context.getString(R.string.onboarding_group_orders_title),
            description = context.getString(R.string.onboarding_group_orders_description),
            drawableRes = R.drawable.undraw_eating_together,
        ),
        TutorialPage(
            title = context.getString(R.string.onboarding_pick_up_delivery_title),
            description = context.getString(R.string.onboarding_pick_up_delivery_description),
            drawableRes = R.drawable.undraw_takeout_boxes,
            showDismiss = true
        )
    )

    private val _dismissTutorial = Channel<Unit>()
    val dismissTutorial: Flow<Unit> = _dismissTutorial.receiveAsFlow()

    fun onCompleteTutorial() = viewModelScope.launch {
        val completed = getUserCompleteTutorialUseCase(Unit).getSuccessDataOr(false)
        if (!completed) {
            updateUserCompleteTutorialUseCase(true)
        }
        _dismissTutorial.offer(Unit)
    }
}

@Parcelize
data class TutorialPage(
    val title: String,
    val description: String,
    @DrawableRes val drawableRes: Int,
    val showDismiss: Boolean = false
) : Parcelable

