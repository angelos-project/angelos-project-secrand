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


class SecureFeedTest {

    @Test
    fun testNextBitsToMuch() {
        try {
            assertFailsWith<IllegalArgumentException>{
                SecureFeed.nextBits(33)
            }
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckWithSample { debug -> analyzeSecurity( SecureFeed , debug) }
        }

    }

    @Test
    fun testSecurityHealth() {
        assertTrue{
            HealthCheck.doubleHealthCheckDebug { debug ->
                analyzeSecurity(SecureFeed, debug)
            }
        }
    }
}