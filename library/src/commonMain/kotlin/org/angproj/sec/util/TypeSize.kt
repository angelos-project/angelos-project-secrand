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
 * Utility object providing constants for the bit and byte sizes of Kotlin primitive types.
 *
 * Includes both signed and unsigned types, as well as floating-point types.
 * Useful for buffer management, serialization, and low-level data manipulation.
 *
 * - `byteBits`, `shortBits`, `intBits`, `longBits`: Number of bits in signed types.
 * - `uByteBits`, `uShortBits`, `uIntBits`, `uLongBits`: Number of bits in unsigned types.
 * - `floatBits`, `doubleBits`: Number of bits in floating-point types.
 * - `uByteSize`, `uShortSize`, `uIntSize`, `uLongSize`: Number of bytes in signed types.
 * - `byteSize`, `shortSize`, `intSize`, `longSize`: Number of bytes in unsigned types.
 * - `floatSize`, `doubleSize`: Number of bytes in floating-point types.
 */
public object TypeSize {
    public const val byteBits: Int = Byte.SIZE_BITS
    public const val shortBits: Int = Short.SIZE_BITS
    public const val intBits: Int = Int.SIZE_BITS
    public const val longBits: Int = Long.SIZE_BITS

    public const val uByteBits: Int = UByte.SIZE_BITS
    public const val uShortBits: Int = UShort.SIZE_BITS
    public const val uIntBits: Int = UInt.SIZE_BITS
    public const val uLongBits: Int = ULong.SIZE_BITS

    public const val floatBits: Int = Float.SIZE_BITS
    public const val doubleBits: Int = Double.SIZE_BITS

    public const val uByteSize: Int = Byte.SIZE_BYTES
    public const val uShortSize: Int = Short.SIZE_BYTES
    public const val uIntSize: Int = Int.SIZE_BYTES
    public const val uLongSize: Int = Long.SIZE_BYTES

    public const val byteSize: Int = UByte.SIZE_BYTES
    public const val shortSize: Int = UShort.SIZE_BYTES
    public const val intSize: Int = UInt.SIZE_BYTES
    public const val longSize: Int = ULong.SIZE_BYTES

    public const val floatSize: Int = Float.SIZE_BYTES
    public const val doubleSize: Int = Double.SIZE_BYTES
}