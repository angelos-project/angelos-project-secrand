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

/**
 * The `TypeSize` object provides a comprehensive set of constants representing the bit and byte sizes
 * of Kotlin's primitive types. This utility is designed to facilitate operations that depend on knowing
 * the exact size of data types, such as serialization, buffer management, and interoperability with
 * lower-level systems or protocols.
 *
 * The constants are grouped by type category: signed integers, unsigned integers, and floating-point types.
 * For each type, both the number of bits and the number of bytes are provided, allowing for flexible
 * calculations and conversions.
 */
public object TypeSize {
    // Signed integer types

    /**
     * Total number of bits in a Byte is 8, which derives from `Byte.SIZE_BITS`.
     */
    public const val byteBits: Int = Byte.SIZE_BITS

    /**
     * Total number of bits in a Short is 16, which derives from `Short.SIZE_BITS`.
     */
    public const val shortBits: Int = Short.SIZE_BITS

    /**
     * Total number of bits in an Int is 32, which derives from `Int.SIZE_BITS`.
     */
    public const val intBits: Int = Int.SIZE_BITS

    /**
     * Total number of bits in a Long is 64, which derives from `Long.SIZE_BITS`.
     */
    public const val longBits: Int = Long.SIZE_BITS

    // Unsigned integer types

    /**
     * Total number of bits in a UByte is 8, which derives from `UByte.SIZE_BITS`.
     */
    public const val uByteBits: Int = UByte.SIZE_BITS

    /**
     * Total number of bits in a UShort is 16, which derives from `UShort.SIZE_BITS`.
     */
    public const val uShortBits: Int = UShort.SIZE_BITS

    /**
     * Total number of bits in a UInt is 32, which derives from `UInt.SIZE_BITS`.
     */
    public const val uIntBits: Int = UInt.SIZE_BITS

    /**
     * Total number of bits in a ULong is 64, which derives from `ULong.SIZE_BITS`.
     */
    public const val uLongBits: Int = ULong.SIZE_BITS

    // Floating-point types

    /**
     * Total number of bits in a Float is 32, which derives from `Float.SIZE_BITS`.
     */
    public const val floatBits: Int = Float.SIZE_BITS

    /**
     * Total number of bits in a Double is 64, which derives from `Double.SIZE_BITS`.
     */
    public const val doubleBits: Int = Double.SIZE_BITS

    // Signed integer types

    /**
     * Total number of bytes in a Byte is 1, which derives from `Byte.SIZE_BYTES`.
     */
    public const val byteSize: Int = Byte.SIZE_BYTES

    /**
     * Total number of bytes in a Short is 2, which derives from `Short.SIZE_BYTES`.
     */
    public const val shortSize: Int = Short.SIZE_BYTES

    /**
     * Total number of bytes in an Int is 4, which derives from `Int.SIZE_BYTES`.
     */
    public const val intSize: Int = Int.SIZE_BYTES

    /**
     * Total number of bytes in a Long is 4, which derives from `Long.SIZE_BYTES`.
     */
    public const val longSize: Int = Long.SIZE_BYTES

    // Unsigned integer types

    /**
     * Total number of bytes in a UByte is 1, which derives from `UByte.SIZE_BYTES`.
     */
    public const val uByteSize: Int = UByte.SIZE_BYTES

    /**
     * Total number of bytes in a UShort is 2, which derives from `UShort.SIZE_BYTES`.
     */
    public const val uShortSize: Int = UShort.SIZE_BYTES

    /**
     * Total number of bytes in a UInt is 4, which derives from `UInt.SIZE_BYTES`.
     */
    public const val uIntSize: Int = UInt.SIZE_BYTES

    /**
     * Total number of bytes in a ULong is 8, which derives from `ULong.SIZE_BYTES`.
     */
    public const val uLongSize: Int = ULong.SIZE_BYTES

    // Floating-point types

    /**
     * Total number of bytes in a Float is 4, which derives from `Float.SIZE_BYTES`.
     */
    public const val floatSize: Int = Float.SIZE_BYTES

    /**
     * Total number of bytes in a Double is 8, which derives from `Double.SIZE_BYTES`.
     */
    public const val doubleSize: Int = Double.SIZE_BYTES
}