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

import org.angproj.sec.SecureFeed
import org.angproj.sec.SecureRandomException
import org.angproj.sec.rand.JitterEntropy
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SecurelyRandomizeTest {

    @Test
    fun testSecurelyRandomizeByteArray() {
        try {
            val buffer = ByteArray(1024)

            buffer.securelyRandomize()

            assertTrue(HealthCheck.healthCheck { _ -> analyzeByteArray(buffer) })
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckDebug { sample -> analyzeLongs(SecureFeed::readLongs, sample) }
        }
    }

    @Test
    fun testSecurelyEntropizeByteArray() {
        try {
            val buffer = ByteArray(1024)

            buffer.securelyEntropize()

            assertTrue(HealthCheck.healthCheck { _ -> analyzeByteArray(buffer) })
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckDebug { sample -> analyzeLongs(JitterEntropy::readLongs, sample) }
        }
    }

    @Test
    fun testSecurelyRandomizeSmallSizes() {
        repeat(32) {
            val array = ByteArray(it+1).also { it.securelyRandomize() }
            assertNotEquals(array.sum(), 0)
        }
    }

    @Test
    fun testSecurelyEntropizeZeroSize() {
        try {
            ByteArray(0).securelyEntropize()
        } catch (_: Exception) {
            assertTrue(false)
        }
    }

    @Test
    fun testSecurelyRandomizeZeroSize() {
        try {
            ByteArray(0).securelyRandomize()
        } catch (_: Exception) {
            assertTrue(false)
        }
    }
}