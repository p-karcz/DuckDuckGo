#!/bin/bash

# Adapted from:
#
# # A small bash script which performs the entire jank performance test set up and execution.
# #
# # Created by Andre Perkins (akperkins1@gmail.com) on 6/6/17.
# #
# # Assumptions:
# # - adb is available via system PATH

function main() {
    local -r projectRoot=$(git rev-parse --show-toplevel)

     "${projectRoot}"/gradlew -p "${projectRoot}" :app:assembleInternalDebug :app:assembleInternalDebugAndroidTest
     adb install -r "${projectRoot}"/app/build/outputs/apk/internal/debug/duckduckgo-5.107.0-internal-debug.apk
     adb install -r "${projectRoot}"/app/build/outputs/apk/androidTest/internal/debug/duckduckgo-5.107.0-internal-debug-androidTest.apk


    # For finding package name, use
    # adb shell dumpsys window | grep -E 'mCurrentFocus'
    # while running the app in emulator.
    # The scheme is PackageName/ActivityName.

    local -r packageName="com.duckduckgo.mobile.android.debug"

    # To run specific test just add #test_name after first path. eg. com.duckduckgo.espresso.BasicKotlinTest#browser_openPopUp
    local -r instrumentArguments="adb shell am instrument -w -m  --no-window-animation  -e debug false -e class 'com.duckduckgo.espresso.BasicKotlinTest' com.duckduckgo.mobile.android.debug.test/androidx.test.runner.AndroidJUnitRunner"

    local -r instrumentArguments2="adb shell am instrument -w -m  --no-window-animation  -e debug false -e class 'com.duckduckgo.uiautomator.BasicTest' com.duckduckgo.mobile.android.debug.test/androidx.test.runner.AndroidJUnitRunner"

    # Executes kotlin script which queries gfxinfo while running the automated tests
    kotlinc main.kt -include-runtime -d main.jar
    java -jar main.jar "$packageName" "$instrumentArguments" "$instrumentArguments2"
}

main
