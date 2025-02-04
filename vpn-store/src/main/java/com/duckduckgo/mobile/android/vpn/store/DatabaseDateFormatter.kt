/*
 * Copyright (c) 2020 DuckDuckGo
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

package com.duckduckgo.mobile.android.vpn.store

import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class DatabaseDateFormatter {

    companion object {
        private val FORMATTER_SECONDS: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        fun bucketByHour(date: LocalDateTime = LocalDateTime.now()): String {
            val byHour = date
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
            return FORMATTER_SECONDS.format(byHour)
        }

        fun timestamp(date: LocalDateTime = LocalDateTime.now()): String {
            return FORMATTER_SECONDS.format(date)
        }

        fun duration(
            start: String,
            end: String = FORMATTER_SECONDS.format(LocalDateTime.now())
        ): Duration {
            val startTime = LocalDateTime.parse(start, FORMATTER_SECONDS)
            val endTime = LocalDateTime.parse(end, FORMATTER_SECONDS)
            return Duration.between(startTime, endTime)
        }
    }
}
