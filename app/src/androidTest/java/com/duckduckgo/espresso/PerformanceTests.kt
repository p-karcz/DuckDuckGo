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

package com.duckduckgo.espresso;

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PerformanceTests {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun browserOpenPopUp() {
        for(i in 1..5){
            onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))
            Espresso.closeSoftKeyboard()

            onView(withId(R.id.browserMenu)).perform(click())

            onView(isRoot()).perform(waitForView(withId(R.id.settingsPopupMenuItem)))
            onView(withId(R.id.settingsPopupMenuItem)).perform(click())

            onView(isRoot()).perform(waitForView(withId(R.id.includeSettings)))
            onView(withId(R.id.includeSettings)).check(matches(isDisplayed()))
            onView(withId(R.id.includeSettings)).perform(swipeUp())

            pressBack()
        }
    }
}
