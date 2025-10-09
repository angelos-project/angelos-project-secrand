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

import org.angproj.sec.util.Octet.asHexSymbols
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals

class OctetTest {

    @Test
    fun testReverseOrderReadWrite() {
        (2 .. 8).forEach { size ->
            val testData = 0x1122334455667788L ushr (64 - (size * 8))

            val buffer = ByteArray(8)
            Octet.writeBE(testData, buffer, 0, size) { index, value ->
                buffer[index] = value
            }

            val readLong = Octet.readBE(buffer, 0, size) { index ->
                buffer[index]
            }

            assertEquals(testData, readLong)
        }
    }

    @Test
    fun testLitterEndianOrderReadWrite() {
        (2 .. 8).forEach { size ->
            val testData = 0x1122334455667788L ushr (64 - (size * 8))

            val buffer = ByteArray(8)
            Octet.writeLE(testData, buffer, 0, size) { index, value ->
                buffer[index] = value
            }

            val readLong = Octet.readLE(buffer, 0, size) { index ->
                buffer[index]
            }

            assertEquals(testData, readLong)
        }
    }

    @Test
    fun testWriteOpposite() {
        val testData = 0x1122334455667788L

        val leBuffer = ByteArray(8)
        Octet.writeLE(testData, leBuffer, 0, leBuffer.size) { index, value ->
            leBuffer[index] = value
        }

        val beBuffer = ByteArray(8)
        Octet.writeBE(testData, beBuffer, 0, beBuffer.size) { index, value ->
            beBuffer[index] = value
        }

        beBuffer.reverse()
        assertContentEquals(leBuffer, beBuffer)
    }

    @Test
    fun testAsHexSymbol() {
        assertEquals(byteArrayOf(0x11, 0x22, 0x33, 0x44).asHexSymbols(), "11223344")
    }
}