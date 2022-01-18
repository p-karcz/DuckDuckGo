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

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeavyLoadTest {

    private fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex = 0

            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun clearAllBookmarks() {
        onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))
        onView(withId(R.id.browserMenu)).perform(click())

        onView(isRoot()).perform(waitForView(withId(R.id.bookmarksPopupMenuItem)))
        onView(withId(R.id.bookmarksPopupMenuItem)).perform(click())

        onView(isRoot()).perform(waitForView(withId(R.id.bookmarkRootView)))

        try {
            while (true) {
                val button = onView(withIndex(withId(R.id.overflowMenu), 0))
                button.perform(click())
                onView(withId(R.id.delete)).perform(click())
            }
        } catch (e: NoMatchingViewException) {

        }

        pressBack()

    }

    @Test
    fun oneThousandBookmarks() {

        fun ViewInteraction.isClickable(): Boolean {
            try {
                check(matches(ViewMatchers.isClickable()))
                return true
            } catch (e: Throwable) {
                return false
            }
        }

        for (i in 1..500) {
            onView(isRoot()).perform(waitForView(withId(R.id.omnibarTextInput)))

            val keyboardAwareEditText = onView(withId(R.id.omnibarTextInput))

            keyboardAwareEditText.perform(
                replaceText("https://myanimelist.net/manga/$i/"),
                ViewActions.closeSoftKeyboard(),
                pressImeActionButton(),
            )

            onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))

            val frameLayout = onView(
                Matchers.allOf(
                    withId(R.id.browserMenu),
                    isDisplayed(),
                ),
            )
            frameLayout.perform(click())


            onView(isRoot()).perform(waitForView(withId(R.id.addBookmarksPopupMenuItem)))

            val appCompatTextView = onView(
                Matchers.allOf(
                    withId(R.id.addBookmarksPopupMenuItem),
                    withText("Add Bookmark"),
                ),
            )

            // This is a faster but less stable approach.
            // This approach, for unknown to me reason, will crash the app once in a while.
//            while (!appCompatTextView.isClickable()) {
//
//            }

            // This is a slower but more stable approach.
            // It can still crash if all site fails to load in 1500ms.
            try {
                Thread.sleep(1500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            appCompatTextView.perform(click())
        }
    }
}
