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

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Change device language to Polish before running these tests

@RunWith(AndroidJUnit4::class)
class PolishLanguageTests {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun testAppPolishSettingsTranslations() {
        onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))
        closeSoftKeyboard()

        onView(withId(R.id.browserMenu)).perform(click())
        onView(isRoot()).perform(waitForView(withId(R.id.settingsPopupMenuItem)))
        onView(withId(R.id.settingsPopupMenuItem)).perform(click())

        onView(withId(R.id.selectedThemeSetting)).check(matches(withText("Motyw")))
        onView(withId(R.id.autocompleteToggle)).check(matches(withText("Pokaż sugestie autouzupełniania")))
        onView(withId(R.id.setAsDefaultBrowserSetting)).check(matches(withText("Ustaw jako domyślną przeglądarkę")))
        onView(withId(R.id.changeAppIconLabel)).check(matches(withText("Ikona aplikacji")))
        onView(withId(R.id.accessibilitySetting)).check(matches(withText("Dostępność")))
    }
}
