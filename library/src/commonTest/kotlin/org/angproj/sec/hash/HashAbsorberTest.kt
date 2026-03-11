/**
 * Copyright (c) 2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.sec.hash

import org.angproj.sec.Stubs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HashAbsorberTest {
    @Test
    fun testSqueezeMode() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge(), 1, HashHelper.HashMode.SQUEEZING)
        val absorber = hashHelper.absorber

        assertFailsWith<IllegalStateException> {
            absorber.absorb(1L)
        }
    }

    @Test
    fun testAbsorbOne() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge())
        val absorber = hashHelper.absorber

        absorber.absorb(1)

        assertEquals(hashHelper.forwards, 1)
        assertEquals(hashHelper.position, 1)
    }

    @Test
    fun testAbsorbOf() {
        val sponge = Stubs.stubSucceedSqueezeSponge()
        val absorber = sponge.absorberOf { 1L }

        val position = absorber()

        assertEquals(position, 1)
    }
}