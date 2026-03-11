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
package org.angproj.sec.util

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SecurelyRandomizeTest {

    @Test
    fun testSecurelyRandomizeByteArray() {
        val buffer = ByteArray(1024)

        buffer.securelyRandomize()

        assertTrue(HealthCheck.doubleHealthCheckWithSample { _ -> analyzeByteArray(buffer) })
    }

    @Test
    fun testSecurelyEntropizeByteArray() {
        val buffer = ByteArray(1024)

        buffer.securelyEntropize()

        assertTrue(HealthCheck.doubleHealthCheckWithSample { _ -> analyzeByteArray(buffer) })
    }

    @Test
    fun testSecurelyEntropizeZeroSize() {
        try {
            ByteArray(0).securelyEntropize()
        } catch (_: Exception) {
            assertFalse(true)
        }
    }

    @Test
    fun testSecurelyRandomizeZeroSize() {
        try {
            ByteArray(0).securelyRandomize()
        } catch (_: Exception) {
            assertFalse(true)
        }
    }
}