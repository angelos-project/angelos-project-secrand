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
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class Hash64Test {
    @Test
    fun testBitSize() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())

        assertEquals(256, hash64.bitSize)
    }

    @Test
    fun testByteSize() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())

        assertEquals(32, hash64.byteSize)
    }

    @Test
    fun testVisibleSize() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())

        assertEquals(4, hash64.visibleSize)
    }

    @Test
    fun testHash64() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())

        try {
            hash64.init()
            hash64.update(longArrayOf(), 0, 0, ) { _ -> 0L }
            hash64.final(longArrayOf(), 0, hash64.visibleSize) { _, _ -> }
        } catch (_: Exception) {
            assertFalse(true)
        }
    }

    @Test
    fun testInitError() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())
        hash64.init()
        assertFailsWith<IllegalStateException> {
            hash64.init()
        }
    }

    @Test
    fun testUpdateError() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())
        assertFailsWith<IllegalStateException> {
            hash64.update(longArrayOf(), 0, 0, ) { _ -> 0L }
        }
    }

    @Test
    fun testFinalError() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())
        assertFailsWith<IllegalStateException> {
            hash64.final(longArrayOf(), 0, hash64.visibleSize) { _, _ -> }
        }
    }

    @Test
    fun testReset() {
        val hash64 = Hash64(Stubs.stubSucceedSqueezeSponge())
        hash64.init()

        try {
            hash64.reset()
            hash64.init()
        } catch (_: Exception) {
            assertFalse(true)
        }
    }
}