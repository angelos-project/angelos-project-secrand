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
package org.angproj.sec.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypeCategoryTest {

    @Test
    fun testByte() {
        assertEquals(TypeCategory.BYTE.bitSize, TypeSize.byteBits)
        assertEquals(TypeCategory.BYTE.byteSize, TypeSize.byteSize)

        assertEquals(TypeCategory.ofType(Byte), TypeCategory.BYTE)
        assertEquals(TypeCategory.ofType(1.toByte()), TypeCategory.BYTE)
    }

    @Test
    fun testShort() {
        assertEquals(TypeCategory.SHORT.bitSize, TypeSize.shortBits)
        assertEquals(TypeCategory.SHORT.byteSize, TypeSize.shortSize)

        assertEquals(TypeCategory.ofType(Short), TypeCategory.SHORT)
        assertEquals(TypeCategory.ofType(2.toShort()), TypeCategory.SHORT)
    }

    @Test
    fun testInt() {
        assertEquals(TypeCategory.INT.bitSize, TypeSize.intBits)
        assertEquals(TypeCategory.INT.byteSize, TypeSize.intSize)

        assertEquals(TypeCategory.ofType(Int), TypeCategory.INT)
        assertEquals(TypeCategory.ofType(3.toInt()), TypeCategory.INT)
    }

    @Test
    fun testLong() {
        assertEquals(TypeCategory.LONG.bitSize, TypeSize.longBits)
        assertEquals(TypeCategory.LONG.byteSize, TypeSize.longSize)

        assertEquals(TypeCategory.ofType(Long), TypeCategory.LONG)
        assertEquals(TypeCategory.ofType(4L), TypeCategory.LONG)
    }

    @Test
    fun testFloat() {
        assertEquals(TypeCategory.FLOAT.bitSize, TypeSize.floatBits)
        assertEquals(TypeCategory.FLOAT.byteSize, TypeSize.floatSize)

        assertEquals(TypeCategory.ofType(Float), TypeCategory.FLOAT)
        assertEquals(TypeCategory.ofType(5f), TypeCategory.FLOAT)
    }

    @Test
    fun testDouble() {
        assertEquals(TypeCategory.DOUBLE.bitSize, TypeSize.doubleBits)
        assertEquals(TypeCategory.DOUBLE.byteSize, TypeSize.doubleSize)

        assertEquals(TypeCategory.ofType(Double), TypeCategory.DOUBLE)
        assertEquals(TypeCategory.ofType(6.0), TypeCategory.DOUBLE)
    }

    @Test
    fun testUByte() {
        assertEquals(TypeCategory.U_BYTE.bitSize, TypeSize.uByteBits)
        assertEquals(TypeCategory.U_BYTE.byteSize, TypeSize.uByteSize)

        assertEquals(TypeCategory.ofType(UByte), TypeCategory.U_BYTE)
        assertEquals(TypeCategory.ofType(7.toUByte()), TypeCategory.U_BYTE)
    }

    @Test
    fun testUShort() {
        assertEquals(TypeCategory.U_SHORT.bitSize, TypeSize.uShortBits)
        assertEquals(TypeCategory.U_SHORT.byteSize, TypeSize.uShortSize)

        assertEquals(TypeCategory.ofType(UShort), TypeCategory.U_SHORT)
        assertEquals(TypeCategory.ofType(8.toUShort()), TypeCategory.U_SHORT)
    }

    @Test
    fun testUInt() {
        assertEquals(TypeCategory.U_INT.bitSize, TypeSize.intBits)
        assertEquals(TypeCategory.U_INT.byteSize, TypeSize.intSize)

        assertEquals(TypeCategory.ofType(UInt), TypeCategory.U_INT)
        assertEquals(TypeCategory.ofType(9.toUInt()), TypeCategory.U_INT)
    }

    @Test
    fun testULong() {
        assertEquals(TypeCategory.U_LONG.bitSize, TypeSize.uLongBits)
        assertEquals(TypeCategory.U_LONG.byteSize, TypeSize.uLongSize)

        assertEquals(TypeCategory.ofType(ULong), TypeCategory.U_LONG)
        assertEquals(TypeCategory.ofType(4uL), TypeCategory.U_LONG)
    }

    @Test
    fun testUnsupportedType() {
        assertFailsWith<IllegalStateException> {
            TypeCategory.ofType("Unsupported type")
        }
    }
}