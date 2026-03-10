/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.sec

import org.angproj.sec.util.HealthCheck
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.assertFalse


class SecureFeedTest {

    @Test
    fun testNextBits() {
        try {
            SecureFeed.nextBits(21).countLeadingZeroBits()
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckWithSample { debug -> analyzeSecurity( SecureFeed , debug) }
        } catch (_: Exception) {
            assertFalse(true)
        }
    }

    @Test
    fun testNextBitsToMuch() {
        try {
            assertFailsWith<IllegalArgumentException>{
                SecureFeed.nextBits(33)
            }
            assertFailsWith<IllegalArgumentException>{
                SecureFeed.nextBits(-1)
            }
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckWithSample { debug -> analyzeSecurity( SecureFeed , debug) }
        }
    }

    @Test
    fun testSecurityHealth() {
        assertTrue{
            HealthCheck.doubleHealthCheckWithSample { debug ->
                analyzeSecurity(SecureFeed, debug)
            }
        }
    }

    @Test
    @Suppress
    fun testHealthCheck() {
        SecureFeed.checkSecurityHealth()
    }
}