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

class HashSqueezerTest {
    @Test
    fun testAbsorbMode() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge(), 1)
        val squeezer = hashHelper.squeezer

        assertFailsWith<IllegalStateException> {
            squeezer.squeeze()
        }
    }

    @Test
    fun testSqueezeOne() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge(), 0, HashHelper.HashMode.SQUEEZING)
        val squeezer = hashHelper.squeezer

        squeezer.squeeze()

        assertEquals(hashHelper.forwards, 1)
        assertEquals(hashHelper.position, 1)
    }
}