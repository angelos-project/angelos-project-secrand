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
package org.angproj.sec.rand

import org.mockito.Mock
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class SpongeMockitoTest {

    @Mock
    var spongeMock: Sponge = mock()

    @Test
    fun testSpongeSize() {
        whenever(spongeMock.spongeSize).thenReturn(9)
        assertEquals(spongeMock.spongeSize, 9)
    }

    @Test
    fun testVisibleSize() {
        whenever(spongeMock.visibleSize).thenReturn(8)
        assertEquals(spongeMock.visibleSize, 8)
    }

    @Test
    fun testByteSize() {
        whenever(spongeMock.byteSize).thenReturn(64)
        assertEquals(spongeMock.byteSize, 8 * Long.SIZE_BYTES)
    }

    @Test
    fun testReset() {
        doNothing().whenever(spongeMock).reset()
        assertEquals(spongeMock.reset(), Unit)
    }

    @Test
    fun testRound() {
        doNothing().whenever(spongeMock).round()
        assertEquals(spongeMock.round(), Unit)
    }

    @Test
    fun testAbsorb() {
        doNothing().whenever(spongeMock).absorb(0, 1)
        assertEquals(spongeMock.absorb(0, 1), Unit)
    }

    @Test
    fun testSqueeze() {
        whenever(spongeMock.squeeze(0)).thenReturn(0x11)
        assertEquals(spongeMock.squeeze(0), 0x11)
    }

    @Test
    fun testScramble() {
        doNothing().whenever(spongeMock).scramble()
        assertEquals(spongeMock.scramble(), Unit)
    }
}