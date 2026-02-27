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
    fun testBitSizeFunction() {
        assertEquals(TypeCategory.fromType(1.toByte()).bitSize, TypeSize.bitSize(1.toByte()))
        assertEquals(TypeCategory.fromType(2.toShort()).bitSize, TypeSize.bitSize(2.toShort()))
        assertEquals(TypeCategory.fromType(3).bitSize, TypeSize.bitSize(3))
        assertEquals(TypeCategory.fromType(4.toLong()).bitSize, TypeSize.bitSize(4.toLong()))
        assertEquals(TypeCategory.fromType(5.toFloat()).bitSize, TypeSize.bitSize(5.toFloat()))
        assertEquals(TypeCategory.fromType(6.0).bitSize, TypeSize.bitSize(6.0))
        assertEquals(TypeCategory.fromType(7.toUByte()).bitSize, TypeSize.bitSize(7.toUByte()))
        assertEquals(TypeCategory.fromType(8.toUShort()).bitSize, TypeSize.bitSize(8.toUShort()))
        assertEquals(TypeCategory.fromType(9.toUInt()).bitSize, TypeSize.bitSize(9.toUInt()))
        assertEquals(TypeCategory.fromType(10.toULong()).bitSize, TypeSize.bitSize(10.toULong()))

        assertFailsWith<IllegalStateException> {
            TypeCategory.fromType("Unsupported type")
        }
    }

    @Test
    fun testByteSizeFunction() {
        assertEquals(TypeCategory.fromType(1.toByte()).byteSize, TypeSize.byteSize(1.toByte()))
        assertEquals(TypeCategory.fromType(2.toShort()).byteSize, TypeSize.byteSize(2.toShort()))
        assertEquals(TypeCategory.fromType(3).byteSize, TypeSize.byteSize(3))
        assertEquals(TypeCategory.fromType(4.toLong()).byteSize, TypeSize.byteSize(4.toLong()))
        assertEquals(TypeCategory.fromType(5.toFloat()).byteSize, TypeSize.byteSize(5.toFloat()))
        assertEquals(TypeCategory.fromType(6.0).byteSize, TypeSize.byteSize(6.0))
        assertEquals(TypeCategory.fromType(7.toUByte()).byteSize, TypeSize.byteSize(7.toUByte()))
        assertEquals(TypeCategory.fromType(8.toUShort()).byteSize, TypeSize.byteSize(8.toUShort()))
        assertEquals(TypeCategory.fromType(9.toUInt()).byteSize, TypeSize.byteSize(9.toUInt()))
        assertEquals(TypeCategory.fromType(10.toULong()).byteSize, TypeSize.byteSize(10.toULong()))

        assertFailsWith<IllegalStateException> {
            TypeCategory.fromType("Unsupported type")
        }
    }
}