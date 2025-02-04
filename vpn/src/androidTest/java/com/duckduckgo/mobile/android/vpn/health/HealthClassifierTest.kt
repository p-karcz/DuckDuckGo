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

package com.duckduckgo.mobile.android.vpn.health

import androidx.test.platform.app.InstrumentationRegistry
import com.duckduckgo.mobile.android.vpn.health.AppTPHealthMonitor.HealthState
import com.duckduckgo.mobile.android.vpn.health.AppTPHealthMonitor.HealthState.*
import com.duckduckgo.mobile.android.vpn.health.TracerPacketRegister.TracerSummary
import com.duckduckgo.mobile.android.vpn.health.TracerPacketRegister.TracerSummary.Completed
import com.duckduckgo.mobile.android.vpn.health.TracerPacketRegister.TracerSummary.Invalid
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class HealthClassifierTest {

    private val testee = HealthClassifier(InstrumentationRegistry.getInstrumentation().targetContext)
    val tracers = mutableListOf<TracerSummary>()

    @Test
    fun whenNotManyTracerPacketsThenReportsInitializing() {
        val numberInvalid = 0
        val numberSlowTracers = 0
        val totalTracers = 0

        // no tracers at all
        configureTracers(numberInvalid, numberSlowTracers, totalTracers)

        val state = testee.determineHealthTracerPackets(emptyList())
        state.assertInitializing()
    }

    @Test
    fun whenEnoughGoodTracerPacketsThenDoesNotReportInitializingState() {
        val numberInvalid = 0
        val numberSlowTracers = 0
        val totalTracers = 10

        // 0% failure rate
        configureTracers(numberInvalid, numberSlowTracers, totalTracers)

        val state = testee.determineHealthTracerPackets(tracers)
        state.assertGoodHealth()
    }

    @Test
    fun whenSomeInvalidTracerPacketsBelowThresholdThenReportsGoodHealth() {
        val numberInvalid = 4
        val numberSlowTracers = 0
        val totalTracers = 100

        // 4% failure rate
        configureTracers(numberInvalid, numberSlowTracers, totalTracers)

        val state = testee.determineHealthTracerPackets(tracers)
        state.assertGoodHealth()
    }

    @Test
    fun whenInvalidTracersMakeFailureRateTooHighThenReportsBadHealth() {
        val numberInvalid = 16
        val numberSlowTracers = 0
        val totalTracers = 100

        // 16% failure rate
        configureTracers(numberInvalid, numberSlowTracers, totalTracers)
        val state = testee.determineHealthTracerPackets(tracers)
        state.assertBadHealth()
    }

    @Test
    fun whenSomeSlowTracerPacketsBelowThresholdThenReportsGoodHealth() {
        val numberInvalid = 0
        val numberSlowTracers = 4
        val totalTracers = 100

        // 4% failure rate
        configureTracers(numberInvalid, numberSlowTracers, totalTracers)

        val state = testee.determineHealthTracerPackets(tracers)
        state.assertGoodHealth()
    }

    @Test
    fun whenSlowTracersMakeFailureRateTooHighThenReportsBadHealth() {
        val numberInvalid = 0
        val numberSlowTracers = 16
        val totalTracers = 100

        // 16% failure rate
        configureTracers(numberInvalid, numberSlowTracers, totalTracers)
        val state = testee.determineHealthTracerPackets(tracers)
        state.assertBadHealth()
    }

    @Test
    fun whenNumberOfSocketReadExceptionsBelowThresholdThenReportsGoodHealth() {
        testee.determineHealthSocketChannelReadExceptions(ACCEPTABLE_NUMBER_SOCKET_EXCEPTIONS).assertGoodHealth()
    }

    @Test
    fun whenNumberOfSocketReadExceptionsAboveThresholdThenReportsBadHealth() {
        testee.determineHealthSocketChannelReadExceptions(EXCESSIVE_NUMBER_SOCKET_EXCEPTIONS).assertBadHealth()
    }

    @Test
    fun whenNumberOfSocketWriteExceptionsBelowThresholdThenReportsGoodHealth() {
        testee.determineHealthSocketChannelWriteExceptions(ACCEPTABLE_NUMBER_SOCKET_EXCEPTIONS).assertGoodHealth()
    }

    @Test
    fun whenNumberOfSocketWriteExceptionsAboveThresholdThenReportsGoodHealth() {
        testee.determineHealthSocketChannelWriteExceptions(EXCESSIVE_NUMBER_SOCKET_EXCEPTIONS).assertBadHealth()
    }

    @Test
    fun whenNumberOfSocketConnectExceptionsBelowThresholdThenReportsGoodHealth() {
        testee.determineHealthSocketChannelConnectExceptions(ACCEPTABLE_NUMBER_SOCKET_EXCEPTIONS).assertGoodHealth()
    }

    @Test
    fun whenNumberOfSocketConnectExceptionsAboveThresholdThenReportsBadHealth() {
        testee.determineHealthSocketChannelConnectExceptions(EXCESSIVE_NUMBER_SOCKET_EXCEPTIONS).assertBadHealth()
    }

    @Test
    fun whenTooFewTunInputsThenReportsInitializing() {
        val tunInputs: Long = 0
        val queueReads = QueueReads(0, 0, 0)
        testee.determineHealthTunInputQueueReadRatio(tunInputs, queueReads).assertInitializing()
    }

    @Test
    fun whenLotsOfTunInputsButNoQueueReadsThenReportsBadHealth() {

        // success rate: 0%
        val tunInputs: Long = 100
        val queueReads = QueueReads(0, 0, 0)

        testee.determineHealthTunInputQueueReadRatio(tunInputs, queueReads).assertBadHealth()
    }

    @Test
    fun whenLotsOfTunInputsAndLowNumberOfQueueReadsThenReportsBadHealth() {

        // success rate: 10%
        val tunInputs: Long = 900
        val queueReads = QueueReads(100, 100, 0)

        testee.determineHealthTunInputQueueReadRatio(tunInputs, queueReads).assertBadHealth()
    }

    @Test
    fun whenLotsOfTunInputsAndQueueReadsThenReportsBadHealth() {

        // success rate: 100%
        val tunInputs: Long = 900
        val queueReads = QueueReads(900, 900, 0)

        testee.determineHealthTunInputQueueReadRatio(tunInputs, queueReads).assertGoodHealth()
    }

    @Test
    fun whenTunInputsToQueueReadRateIsOver100PercentThenIsStillGoodHealth() {

        // success rate: 900%
        val tunInputs: Long = 100
        val queueReads = QueueReads(900, 900, 0)

        testee.determineHealthTunInputQueueReadRatio(tunInputs, queueReads).assertGoodHealth()
    }

    private fun HealthState.assertGoodHealth() {
        assertTrue(String.format("Expected GoodHealth but was %s", this.javaClass.simpleName), (this) is GoodHealth)
    }

    private fun HealthState.assertBadHealth() {
        assertTrue(String.format("Expected BadHealth but was %s", this.javaClass.simpleName), (this) is BadHealth)
    }

    private fun HealthState.assertInitializing() {
        assertTrue(String.format("Expected Initializing but was %s", this.javaClass.simpleName), (this) is Initializing)
    }

    private fun configureTracers(
        numberInvalid: Int,
        numberSlowTracers: Int,
        totalTracers: Int
    ) {
        for (i in 0 until numberInvalid) {
            tracers.add(anInvalidTracer())
        }

        for (i in 0 until numberSlowTracers) {
            tracers.add(aSlowTracer())
        }

        for (i in 0 until (totalTracers - numberInvalid - numberSlowTracers)) {
            tracers.add(aSuccessfulTracer())
        }
    }

    companion object {

        private const val ACCEPTABLE_NUMBER_SOCKET_EXCEPTIONS: Long = 10
        private const val EXCESSIVE_NUMBER_SOCKET_EXCEPTIONS: Long = 100

        private fun anInvalidTracer(): Invalid {
            return Invalid("abc", 0, "invalid")
        }

        private fun aSuccessfulTracer(): Completed {
            return Completed("abc", 0, 20, emptyList())
        }

        private fun aSlowTracer(): Completed {
            return Completed("abc", 0, TimeUnit.MINUTES.toNanos(2), emptyList())
        }
    }
}
