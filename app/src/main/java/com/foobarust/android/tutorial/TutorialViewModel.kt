package com.foobarust.android.tutorial

import android.content.Context
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.utils.SingleLiveEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


class TutorialViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    val tutorialProperties = listOf(
        TutorialProperty(
            title = context.getString(R.string.onboarding_browse_food_title),
            description = context.getString(R.string.onboarding_browse_food_description),
            imageRes = R.drawable.undraw_online_groceries,
        ),
        TutorialProperty(
            title = context.getString(R.string.onboarding_group_orders_title),
            description = context.getString(R.string.onboarding_group_orders_description),
            imageRes = R.drawable.undraw_eating_together,
        ),
        TutorialProperty(
            title = context.getString(R.string.onboarding_pick_up_delivery_title),
            description = context.getString(R.string.onboarding_pick_up_delivery_description),
            imageRes = R.drawable.undraw_takeout_boxes,
            showCompleteButton = true
        )
    )

    private val _dismissTutorial = SingleLiveEvent<Unit>()
    val dismissTutorial: LiveData<Unit>
        get() = _dismissTutorial

    fun onTutorialCompleted() = viewModelScope.launch {
        _dismissTutorial.value = Unit
    }
}

@Parcelize
data class TutorialProperty(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val showCompleteButton: Boolean = false
) : Parcelable

