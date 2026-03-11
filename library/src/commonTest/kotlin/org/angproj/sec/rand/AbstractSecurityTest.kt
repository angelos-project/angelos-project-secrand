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
package org.angproj.sec.rand

import org.angproj.sec.Fakes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse


class AbstractSecurityTest {

    @Test
    fun testCheckBitCount() {
        val sec = Fakes.safeSecRand()
        val bytes = ByteArray(10)

        sec.exportBytes(bytes, 0, bytes.size) { index, value ->
            this[index] = value
        }

        assertEquals(sec.bitCounter, 128)
    }

    @Test
    fun testExportLongsZero() {
        try {
            val sec = Fakes.safeSecRand()

            sec.exportLongs(longArrayOf(), 0, 0) { index, value ->}
        } catch (e: Exception) {
            println(e)
            assertFalse(true)
        }
    }

    @Test
    fun testExportBytesZero() {
        try {
            val sec = Fakes.safeSecRand()

            sec.exportBytes(byteArrayOf(), 0, 0) { index, value ->}
        } catch (e: Exception) {
            println(e)
            assertFalse(true)
        }
    }

    @Test
    fun testCheckSecurityHealth() {
        val sec = Fakes.safeSecRand()

        val result = sec.checkSecurityHealth(1)

        assertEquals(result.size, 3)
    }

    @Test
    fun testNegativeSecurityHealth() {
        val sec = Fakes.safeSecRand()

        assertFailsWith<IllegalArgumentException>{
           sec.checkSecurityHealth(-1)
        }
    }
}