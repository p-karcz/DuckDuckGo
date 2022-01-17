/*
 * Copyright (c) 2022 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.espresso

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AppThemeTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun testAppDarkTheme() {
        onView(ViewMatchers.isRoot()).perform(waitForView(withId(R.id.browserMenu)))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.browserMenu)).perform(click())

        onView(ViewMatchers.isRoot()).perform(waitForView(withId(R.id.settingsPopupMenuItem)))
        onView(withId(R.id.settingsPopupMenuItem)).perform(click())
        onView(withId(R.id.selectedThemeSetting)).perform(click())
        onView(withId(R.id.themeSelectorDark)).perform(click())
        onView(withText("SET THEME")).inRoot(isDialog()).perform(click())

        onView(withId(R.id.toolbar)).check(matches(BackgroundColourMatcher(com.duckduckgo.mobile.android.R.color.newBlack)))
    }

    @Test
    fun testAppLightTheme() {
        onView(ViewMatchers.isRoot()).perform(waitForView(withId(R.id.browserMenu)))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.browserMenu)).perform(click())
        onView(ViewMatchers.isRoot()).perform(waitForView(withId(R.id.settingsPopupMenuItem)))
        onView(withId(R.id.settingsPopupMenuItem)).perform(click())
        onView(withId(R.id.selectedThemeSetting)).perform(click())
        onView(withId(R.id.themeSelectorLight)).perform(click())
        onView(withText("SET THEME")).inRoot(isDialog()).perform(click())

        onView(withId(R.id.toolbar)).check(matches(BackgroundColourMatcher(com.duckduckgo.mobile.android.R.color.white)))
    }

    private class BackgroundColourMatcher(private val color: Int) : TypeSafeMatcher<View>(View::class.java) {
        override fun matchesSafely(item: View): Boolean {
            if (item.background == null) {
                return false
            }
            val resources: Resources = item.context.resources
            val colourFromResources = ResourcesCompat.getColor(resources, color, null)
            val mColorFromView = (item.background as ColorDrawable).color
            return mColorFromView == colourFromResources
        }

        override fun describeTo(description: Description) {
            description.appendText("Color did not match $color")
        }
    }
}
