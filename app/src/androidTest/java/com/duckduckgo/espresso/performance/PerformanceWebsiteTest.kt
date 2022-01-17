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

import android.text.format.DateUtils
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import com.duckduckgo.espresso.waitForView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PerformanceWebsiteTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun testBrowserOpenWebsite() {
        for(i in 1..5){
            Espresso.onView(ViewMatchers.isRoot()).perform(waitForView(ViewMatchers.withId(R.id.omnibarTextInput)))

            val keyboardAwareEditText = Espresso.onView(
                ViewMatchers.withId(R.id.omnibarTextInput)
            )
            keyboardAwareEditText.perform(ViewActions.click())

            keyboardAwareEditText.perform(
                ViewActions.replaceText(WEBSITE_URL),
                ViewActions.pressImeActionButton()
            )

            val waitingTime = DateUtils.SECOND_IN_MILLIS

            IdlingPolicies.setMasterPolicyTimeout(waitingTime * 10, TimeUnit.MILLISECONDS)
            IdlingPolicies.setIdlingResourceTimeout(waitingTime * 10, TimeUnit.MILLISECONDS)

            val idlingResource: IdlingResource = ElapsedTimeIdlingResource(waitingTime)
            val registry = IdlingRegistry.getInstance()
            registry.register(idlingResource)

            Espresso.onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.pressBack()

            registry.unregister(idlingResource)
        }
    }

    companion object {
        const val WEBSITE_URL = "wikipedia.com"
    }
}
