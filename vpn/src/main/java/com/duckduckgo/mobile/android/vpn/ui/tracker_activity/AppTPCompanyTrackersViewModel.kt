/*
 * Copyright (c) 2021 DuckDuckGo
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

package com.duckduckgo.mobile.android.vpn.ui.tracker_activity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.duckduckgo.app.global.DefaultDispatcherProvider
import com.duckduckgo.app.global.DispatcherProvider
import com.duckduckgo.app.global.plugins.view_model.ViewModelFactoryPlugin
import com.duckduckgo.di.scopes.AppObjectGraph
import com.duckduckgo.mobile.android.vpn.R
import com.duckduckgo.mobile.android.vpn.model.VpnTracker
import com.duckduckgo.mobile.android.vpn.stats.AppTrackerBlockingStatsRepository
import com.duckduckgo.mobile.android.vpn.time.TimeDiffFormatter
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime

class AppTPCompanyTrackersViewModel
@Inject constructor(
    private val statsRepository: AppTrackerBlockingStatsRepository,
    private val timeDiffFormatter: TimeDiffFormatter,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : ViewModel() {

    private val tickerChannel = MutableStateFlow(System.currentTimeMillis())

    suspend fun getTrackersForAppFromDate(date: String, packageName: String): Flow<ViewState> =
        withContext(dispatchers.io()) {
            return@withContext statsRepository
                .getTrackersForAppFromDate(date, packageName)
                .combine(tickerChannel.asStateFlow()) { trackers, _ -> trackers }
                .map { aggregateDataPerApp(it) }
                .flowOn(Dispatchers.Default)
        }

    private fun aggregateDataPerApp(trackerData: List<VpnTracker>): ViewState {
        val sourceData = mutableListOf<CompanyTrackingDetails>()
        val trackerCompany = trackerData.groupBy { it.trackerCompanyId }

        trackerCompany.forEach { data ->
            val trackerCompanyName = data.value[0].company
            val trackerCompanyDisplayName = data.value[0].companyDisplayName
            val timestamp = data.value[0].timestamp

            sourceData.add(
                CompanyTrackingDetails(
                    companyName = trackerCompanyName,
                    companyDisplayName = trackerCompanyDisplayName,
                    trackingAttempts = data.value.size,
                    timestamp = timestamp,
                    trackingSignals = mapTrackingSignals()
                )
            )
        }

        val lastTrackerBlockedAgo =
            timeDiffFormatter.formatTimePassed(
                LocalDateTime.now(), LocalDateTime.parse(sourceData[0].timestamp)
            )

        return ViewState(trackerData.size, lastTrackerBlockedAgo, sourceData)
    }

    private fun mapTrackingSignals(): List<TrackingSignal> {
        val trackingSignals = listOf(TrackingSignal.fromTag("aaid"), TrackingSignal.fromTag("device_id"), TrackingSignal.fromTag("fb_persistent_id"))
        return trackingSignals.distinctBy { it.signalDisplayName }
    }

    data class ViewState(
        val totalTrackingAttempts: Int,
        val lastTrackerBlockedAgo: String,
        val trackingCompanies: List<CompanyTrackingDetails>
    )
    data class CompanyTrackingDetails(
        val companyName: String,
        val companyDisplayName: String,
        val trackingAttempts: Int,
        val timestamp: String,
        val trackingSignals: List<TrackingSignal>
    )
    enum class TrackingSignal(val signaltag: String, @StringRes val signalDisplayName: Int, @DrawableRes val signalIcon: Int) {
        AAID("AAID", R.string.atp_TrackingSignalAAID, R.drawable.ic_signal_id),
        DEVICE_ID("device_id", R.string.atp_TrackingSignalUniqueIdentifier, R.drawable.ic_signal_id),
        FB_PERSISTENT_ID("fb_persistent_id", R.string.atp_TrackingSignalUniqueIdentifier, R.drawable.ic_signal_id);

        companion object {
            fun fromTag(signalTag: String): TrackingSignal {
                return valueOf(signalTag.uppercase())
            }

        }
    }
}

@ContributesMultibinding(AppObjectGraph::class)
class AppTPCompanyTrackersViewModelFactory
@Inject
constructor(private val viewModel: Provider<AppTPCompanyTrackersViewModel>) :
    ViewModelFactoryPlugin {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T? {
        with(modelClass) {
            return when {
                isAssignableFrom(AppTPCompanyTrackersViewModel::class.java) ->
                    (viewModel.get() as T)
                else -> null
            }
        }
    }
}
