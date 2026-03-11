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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SecureEntropyTest {

    @Test
    fun testSecurityHealth() {
        assertTrue{
            HealthCheck.doubleHealthCheckWithSample { debug ->
                analyzeSecurity(SecureEntropy, debug)
            }
        }
    }

    @Test
    fun testHealthCheck() {
        val result = SecureEntropy.checkSecurityHealth(1)

        assertEquals(result.size, 3)
    }
}