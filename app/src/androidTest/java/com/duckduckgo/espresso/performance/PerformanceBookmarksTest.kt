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

package com.duckduckgo.espresso.performance

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import com.duckduckgo.espresso.firstMatch
import com.duckduckgo.espresso.waitForView
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PerformanceBookmarksTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun testCreateDeleteBookmark() {
        Espresso.onView(ViewMatchers.isRoot()).perform(waitForView(ViewMatchers.withId(R.id.omnibarTextInput)))

        val keyboardAwareEditText = Espresso.onView(
            ViewMatchers.withId(R.id.omnibarTextInput)
        )
        keyboardAwareEditText.perform(ViewActions.click())

        keyboardAwareEditText.perform(
            ViewActions.replaceText(WEBSITE_URL),
            ViewActions.pressImeActionButton()
        )

        for (i in 0..10) {
            Espresso.onView(ViewMatchers.isRoot()).perform(waitForView(ViewMatchers.withId(R.id.browserMenu)))
            Espresso.onView(ViewMatchers.withId(R.id.browserMenu)).perform(ViewActions.closeSoftKeyboard(), ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.addBookmarksPopupMenuItem)).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.browserMenu)).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.bookmarksPopupMenuItem)).perform(ViewActions.click())
            Espresso.onView(
                firstMatch(Matchers.allOf(
                ViewMatchers.withId(R.id.overflowMenu),
                ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withId(R.id.recycler))))
            ))).perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.delete)).perform(ViewActions.click())

            Espresso.pressBack()
        }
    }

    companion object {
        const val WEBSITE_URL = "duckduckgo"
    }
}
