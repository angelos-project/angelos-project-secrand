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

import kotlin.test.Test
import kotlin.test.assertEquals

class TypeSizeTest {

    @Test
    fun testSignedIntegerBits() {
        assertEquals(Byte.SIZE_BITS, TypeSize.byteBits)
        assertEquals(Short.SIZE_BITS, TypeSize.shortBits)
        assertEquals(Int.SIZE_BITS, TypeSize.intBits)
        assertEquals(Long.SIZE_BITS, TypeSize.longBits)
    }

    @Test
    fun testUnsignedIntegerBits() {
        assertEquals(UByte.SIZE_BITS, TypeSize.uByteBits)
        assertEquals(UShort.SIZE_BITS, TypeSize.uShortBits)
        assertEquals(UInt.SIZE_BITS, TypeSize.uIntBits)
        assertEquals(ULong.SIZE_BITS, TypeSize.uLongBits)
    }

    @Test
    fun testFloatingPointBits() {
        assertEquals(Float.SIZE_BITS, TypeSize.floatBits)
        assertEquals(Double.SIZE_BITS, TypeSize.doubleBits)
    }

    @Test
    fun testSignedIntegerBytes() {
        assertEquals(Byte.SIZE_BYTES, TypeSize.byteSize)
        assertEquals(Short.SIZE_BYTES, TypeSize.shortSize)
        assertEquals(Int.SIZE_BYTES, TypeSize.intSize)
        assertEquals(Long.SIZE_BYTES, TypeSize.longSize)
    }

    @Test
    fun testUnsignedIntegerBytes() {
        assertEquals(UByte.SIZE_BYTES, TypeSize.uByteSize)
        assertEquals(UShort.SIZE_BYTES, TypeSize.uShortSize)
        assertEquals(UInt.SIZE_BYTES, TypeSize.uIntSize)
        assertEquals(ULong.SIZE_BYTES, TypeSize.uLongSize)
    }

    @Test
    fun testFloatingPointBytes() {
        assertEquals(Float.SIZE_BYTES, TypeSize.floatSize)
        assertEquals(Double.SIZE_BYTES, TypeSize.doubleSize)
    }
}