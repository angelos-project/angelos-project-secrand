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

class HashHelperTest {
    @Test
    fun testHashHelperInitialization() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge())

        assertEquals(hashHelper.forwards, 0)
        assertEquals(hashHelper.position, 0)
        assertEquals(hashHelper.mode, HashHelper.HashMode.ABSORBING)
    }

    @Test
    fun testHashHelperInitializationPosition() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge(), 3)

        assertEquals(hashHelper.forwards, 0)
        assertEquals(hashHelper.position, 3)
        assertEquals(hashHelper.mode, HashHelper.HashMode.ABSORBING)
    }

    @Test
    fun testSwitchMode() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge(), 1)

        hashHelper.switchMode()

        assertEquals(hashHelper.mode, HashHelper.HashMode.SQUEEZING)
        assertEquals(hashHelper.position, 0)
    }

    @Test
    fun testOtherSwitchMode() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge(), 0, HashHelper.HashMode.SQUEEZING)

        hashHelper.switchMode()

        assertEquals(hashHelper.mode, HashHelper.HashMode.ABSORBING)
    }

    @Test
    fun testOneForward() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge())

        hashHelper.forward()

        assertEquals(hashHelper.forwards, 1)
        assertEquals(hashHelper.position, 1)
    }

    @Test
    fun testVisibleSizeForward() {
        val sponge = Stubs.stubSucceedSqueezeSponge()
        val hashHelper = HashHelper(sponge)

        repeat(sponge.visibleSize) {hashHelper.forward()}

        assertEquals(hashHelper.forwards.toInt(), sponge.visibleSize)
        assertEquals(hashHelper.position, 0)
    }

    @Test
    fun testImplicitReset() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge())
        hashHelper.forwards

        hashHelper.reset()

        assertEquals(hashHelper.forwards, 0)
        assertEquals(hashHelper.position, 0)
        assertEquals(hashHelper.mode, HashHelper.HashMode.ABSORBING)
    }

    @Test
    fun testExplicitReset() {
        val hashHelper = HashHelper(Stubs.stubSucceedSqueezeSponge())
        hashHelper.forwards

        hashHelper.reset(HashHelper.HashMode.SQUEEZING)

        assertEquals(hashHelper.forwards, 0)
        assertEquals(hashHelper.position, 0)
        assertEquals(hashHelper.mode, HashHelper.HashMode.SQUEEZING)
    }
}