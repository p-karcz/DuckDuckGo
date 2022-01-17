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
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Change device language to Polish before running these tests

@RunWith(AndroidJUnit4::class)
class PolishLanguageTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>()

    @Test
    fun testPolishSettingsTranslations() {
        onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))
        closeSoftKeyboard()
        onView(withId(R.id.browserMenu)).perform(click())
        onView(isRoot()).perform(waitForView(withId(R.id.settingsPopupMenuItem)))
        onView(withId(R.id.settingsPopupMenuItem)).perform(click())

        checkItemWithIdAndParentId(R.id.settingsGeneralTitle, R.id.contentSettingsGeneral, "OGÓLNE")
        checkItemWithId(R.id.selectedThemeSetting, "Motyw")
        checkItemWithId(R.id.autocompleteToggle, "Pokaż sugestie autouzupełniania")
        checkItemWithId(R.id.setAsDefaultBrowserSetting, "Ustaw jako domyślną przeglądarkę")
        checkItemWithId(R.id.changeAppIconLabel, "Ikona aplikacji")
        checkItemWithId(R.id.accessibilitySetting, "Dostępność")
        checkItemWithIdAndParentId(R.id.settingsPrivacyTitle, R.id.contentSettingsPrivacy, "PRYWATNOŚĆ")
        checkItemWithIdAndGrandparentId(R.id.title, R.id.globalPrivacyControlSetting, "Globalna Kontrola Prywatności (GPC)")
        checkItemWithId(R.id.fireproofWebsites, "Bezpieczne witryny internetowe")
        checkItemWithId(R.id.locationPermissions, "Lokalizacja")
        checkItemWithIdAndGrandparentId(R.id.title, R.id.automaticallyClearWhatSetting, "Usuwaj automatycznie…")
        checkItemWithIdAndGrandparentId(R.id.title, R.id.automaticallyClearWhenSetting, "Usuń w terminie…")
        checkItemWithId(R.id.whitelist, "Niezabezpieczone witryny")
        checkItemWithIdAndGrandparentId(R.id.title, R.id.appLinksSetting, "Otwieranie linków w aplikacjach")
        checkItemWithIdAndGrandGrandparentId(R.id.title, R.id.emailSetting, "Ochrona poczty e-mail")
        checkItemWithIdAndGrandGrandparentId(R.id.title, R.id.deviceShieldSetting, "Ochrona przed śledzącymi aplikacjami")
        checkItemWithId(R.id.settingsOtherTitle, "INNE")
        checkItemWithIdAndParentId(R.id.privacyPolicy, R.id.contentSettingsOther, "Polityka prywatności")
        checkItemWithIdAndParentId(R.id.about, R.id.contentSettingsOther, "O DuckDuckGo")
        checkItemWithIdAndParentId(R.id.provideFeedback, R.id.contentSettingsOther, "Podziel się opinią")
        checkItemWithIdAndGrandparentId(R.id.title, R.id.version, "Wersja")

        pressBack()
    }

    @Test
    fun testPolishHomeScreenTranslations() {
        onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))
        closeSoftKeyboard()
        onView(withId(R.id.browserMenu)).perform(click())

        checkItemWithId(R.id.newTabPopupMenuItem, "Nowa karta")
        checkItemWithId(R.id.bookmarksPopupMenuItem, "Zakładki")
        checkItemWithId(R.id.addBookmarksPopupMenuItem, "Dodaj zakładkę")
        checkItemWithId(R.id.addFavoritePopupMenuItem, "Dodaj do Ulubionych")
        checkItemWithId(R.id.sharePageMenuItem, "Udostępnij…")
        checkItemWithId(R.id.findInPageMenuItem, "Znajdź na stronie")
        checkItemWithId(R.id.addToHome, "Dodaj do ekranu głównego")
        checkItemWithId(R.id.whitelistPopupMenuItem, "Wyłącz ochronę prywatności")
        checkItemWithId(R.id.brokenSitePopupMenuItem, "Zgłoś uszkodzoną witrynę")
        checkItemWithId(R.id.settingsPopupMenuItem, "Ustawienia")
    }

    private fun checkItemWithId(id: Int, text: String) {
        onView(withId(id)).check(matches(withText(text)))
    }

    private fun checkItemWithIdAndParentId(id: Int, parentId: Int, text: String) {
        onView(allOf(withId(id), withParent(withId(parentId))),).check(matches(withText(text)))
    }

    private fun checkItemWithIdAndGrandparentId(id: Int, grandparentId: Int, text: String) {
        onView(allOf(withId(id), withParent(withParent(withId(grandparentId)))),).check(matches(withText(text)))
    }

    private fun checkItemWithIdAndGrandGrandparentId(id: Int, grandGrandparentId: Int, text: String) {
        onView(allOf(withId(id), withParent(withParent(withParent(withId(grandGrandparentId)))))).check(matches(withText(text)))
    }
}
