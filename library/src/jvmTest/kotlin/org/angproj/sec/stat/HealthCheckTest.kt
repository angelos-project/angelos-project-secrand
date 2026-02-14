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
package org.angproj.sec.stat

import org.angproj.sec.GarbageGarbler
import org.angproj.sec.SecureEntropy
import org.angproj.sec.SecureFeed
import org.angproj.sec.rand.RandomBits
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.*

class HealthCheckTest {
    @Test
    fun testGarbageGarbler() {
        try {
            val garbler = GarbageGarbler()
            garbler.securityHealthCheck()
            RandomBits.securityHealthCheck(garbler)
        } catch (_: IllegalStateException) {
            assertFalse(true)
        }
    }

    @Test
    fun testSecureEntropy() {
        try {
            SecureEntropy.securityHealthCheck()
        } catch (_: IllegalStateException) {
            assertFalse(true)
        }
    }

    @Test
    fun testSecureFeed() {
        try {
            SecureFeed.securityHealthCheck()
            RandomBits.securityHealthCheck(SecureFeed)
        } catch (_: IllegalStateException) {
            assertFalse(true)
        }
    }

    @Test
    fun testRandomBits() {
        val staticRand = 0x11223344
        assertFailsWith<IllegalStateException> {
            RandomBits.securityHealthCheck { staticRand }
        }
    }
}