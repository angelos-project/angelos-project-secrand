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

import org.angproj.sec.util.TypeSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals

class MiscTest {

    val intData: Int = 0x11223344
    val byteData: ByteArray = byteArrayOf(0x11, 0x22, 0x33, 0x44)

    @Test
    fun testLittleEndianIntVsByteArray() {
        assertEquals(intData.toString(16), byteData.toHex())
    }

    @Test
    fun testLittleEndianIntToByteArray() {
        val toData = ByteArray(TypeSize.intSize)
        writeLeLong2BeBinary<Unit>(intData.toLong(), toData, 0, 4)
        assertContentEquals(toData, byteData)
    }

    @Test
    fun testByteArrayToLittleEndianInt() {
        val toData = readBeBinary2LeLong<Unit>(byteData, 0, 4).toInt()
        assertEquals(toData, intData)
    }

    @Test
    fun testNumGenerator() {
        assertEquals(TestGen().numGenerator(), "0123456789")
    }

    @Test
    fun testAtoZ2Generator() {
        assertEquals(TestGen().aToZGenerator(), "abcdefghijklmnopqrstuvwxyz")
    }

    @Test
    fun testNopqGenerator() {
        assertEquals(TestGen().nopqGenerator(), "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq")
    }
}