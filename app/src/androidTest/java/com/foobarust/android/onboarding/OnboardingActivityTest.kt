package com.foobarust.android.onboarding

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.foobarust.android.R
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by kevin on 9/6/20
 */

@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<OnboardingActivity>()

    @Test
    fun onboarding_set_up() {
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()))
        onView(withId(R.id.tab_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun onboarding_browse_food() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.onboarding_browse_food_title)
        val description = context.getString(R.string.onboarding_browse_food_description)
        val buttonTitle = context.getString(R.string.onboarding_get_started)

        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(description)).check(matches(isDisplayed()))
        onView(allOf(isDisplayed(), withText(buttonTitle))).check(doesNotExist())
    }

    @Test
    fun onboarding_group_orders() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.onboarding_group_orders_title)
        val description = context.getString(R.string.onboarding_group_orders_description)
        val buttonTitle = context.getString(R.string.onboarding_get_started)

        onView(withId(R.id.view_pager)).perform(swipeLeft())
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(description)).check(matches(isDisplayed()))
        onView(allOf(isDisplayed(), withText(buttonTitle))).check(doesNotExist())
    }

    @Test
    fun onboarding_pick_up_delivery() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.onboarding_pick_up_delivery_title)
        val description = context.getString(R.string.onboarding_pick_up_delivery_description)
        val buttonTitle = context.getString(R.string.onboarding_get_started)

        onView(withId(R.id.view_pager)).perform(swipeLeft())
        onView(withId(R.id.view_pager)).perform(swipeLeft())
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(description)).check(matches(isDisplayed()))
        onView(allOf(isDisplayed(), withText(buttonTitle))).check(matches(isDisplayed()))
    }
}