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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowserSuggestionsTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun testBrowserHints() {
        onView(isRoot()).perform(waitForView(withId(R.id.omnibarTextInput)))

        val keyboardAwareEditText = onView(
            withId(R.id.omnibarTextInput),
        )
        keyboardAwareEditText.perform(click())

        checkHint("abraham lin", "abraham lincoln", keyboardAwareEditText)
        checkHint("goog", "google", keyboardAwareEditText)
        checkHint("wikipe", "wikipedia", keyboardAwareEditText)
        checkHint("faceb", "facebook", keyboardAwareEditText)
        checkHint("albert ein", "albert einstein", keyboardAwareEditText)
    }

    private fun checkHint(input: String, hint: String, keyboardAwareEditText: ViewInteraction) {
        keyboardAwareEditText.perform(
            replaceText(input),
            closeSoftKeyboard(),
        )

        onView(isRoot()).perform(waitForView(withId(R.id.phrase)))
        onView(firstMatch(withId(R.id.phrase))).check(matches(withSubstring(hint)))
        onView(withId(R.id.clearTextButton)).perform(click())
    }
}
