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

/**
 * Enum representing the type category with its corresponding byte and bit sizes.
 *
 * @property byteSize The size of the type in bytes.
 * @property bitSize The size of the type in bits.
 */
public enum class TypeCategory(public val byteSize: Int, public val bitSize: Int) {
    BYTE(TypeSize.byteSize, TypeSize.byteBits),
    SHORT(TypeSize.shortSize, TypeSize.shortBits),
    INT(TypeSize.intSize, TypeSize.intBits),
    LONG(TypeSize.longSize, TypeSize.longBits),
    FLOAT(TypeSize.floatSize, TypeSize.floatBits),
    DOUBLE(TypeSize.doubleSize, TypeSize.doubleBits),
    U_BYTE(TypeSize.uByteSize, TypeSize.uByteBits),
    U_SHORT(TypeSize.uShortSize, TypeSize.uShortBits),
    U_INT(TypeSize.uIntSize, TypeSize.uIntBits),
    U_LONG(TypeSize.uLongSize, TypeSize.uLongBits);

    public companion object {

        /**
         * Returns the [TypeCategory] corresponding to the given type instance.
         *
         * @param type The type instance to determine the category for.
         * @return The [TypeCategory] corresponding to the type instance.
         * @throws IllegalArgumentException If the type is unsupported.
         */
        public fun<E: Any> ofType(type: E): TypeCategory = when (type) {
            Byte, is Byte -> BYTE
            Short, is Short -> SHORT
            Int, is Int -> INT
            Long, is Long -> LONG
            Float, is Float -> FLOAT
            Double, is Double -> DOUBLE
            UByte, is UByte -> U_BYTE
            UShort, is UShort -> U_SHORT
            UInt, is UInt -> U_INT
            ULong, is ULong -> U_LONG
            else -> error("Unsupported type: ${type::class}")
        }
    }
}