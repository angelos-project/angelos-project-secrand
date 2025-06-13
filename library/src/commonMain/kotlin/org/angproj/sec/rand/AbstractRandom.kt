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

import org.angproj.sec.util.ExportOctetByte
import kotlin.math.absoluteValue

public abstract class AbstractRandom : ExportOctetByte {

    protected val buffer: LongArray = LongArray(128)
    protected var position: Int = 0

    protected abstract fun refill()

    /**
     * Revitalizes the random source by reading from the secure feed.
     * This method is called when the internal buffer is exhausted.
     * It fills the buffer with new random data
     * and resets the position to zero.
     */
    protected fun revitalize() {
        refill()
        position = 0
    }

    /**
     * Reads the next byte from the random source.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next byte as a Long value.
     */
    protected inline fun<reified R: Any> nextByte(): Long {
        if (position >= 1024) revitalize()
        val num = buffer[position / 8]
        return (num ushr 8 * (position++ % 8)) and 0xff
    }

    /**
     * Reads the next short integer from the random source.
     * It combines two bytes to form a short value.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next short as a Long value.
     */
    protected inline fun <reified R: Any>nextShort(): Long = (nextByte<Unit>() or (nextByte<Unit>() shl 8)) and 0xffff

    /**
     * Reads the next integer from the random source.
     * It combines four bytes to form an integer value.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next integer as a Long value.
     */
    protected inline fun<reified R: Any> nextInt(): Long = (nextShort<Unit>() or (nextShort<Unit>() shl 16)) and 0xffffffffL

    /**
     * Reads the next long integer from the random source.
     * It combines eight bytes to form a long value.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next long as a Long value.
     */
    protected inline fun<reified R: Any> nextLong(): Long = (nextInt<Unit>() or (nextInt<Unit>() shl 32))


    /**
     * Reads a byte from the random source.
     * The value is normalized to the range [-128, 127].
     *
     * @return A byte in the range [-128, 127].
     */
    public fun readByte(): Byte = nextByte<Unit>().toByte()

    /**
     * Reads an unsigned byte from the random source.
     * The value is normalized to the range [0, 255].
     *
     * @return An unsigned byte in the range [0, 255].
     */
    public fun readUByte(): UByte = nextByte<Unit>().toUByte()

    /**
     * Reads a short integer from the random source.
     * The value is normalized to the range [-32768, 32767].
     *
     * @return A short integer in the range [-32768, 32767].
     */
    public fun readShort(): Short = nextShort<Unit>().toShort()

    /**
     * Reads an unsigned short integer from the random source.
     * The value is normalized to the range [0, 65535].
     *
     * @return An unsigned short integer in the range [0, 65535].
     */
    public fun readUShort(): UShort = nextShort<Unit>().toUShort()

    /**
     * Reads an integer from the random source.
     * The value is normalized to the range [-2147483648, 2147483647].
     *
     * @return An integer in the range [-2147483648, 2147483647].
     */
    public fun readInt(): Int = nextInt<Unit>().toInt()

    /**
     * Reads an unsigned integer from the random source.
     * The value is normalized to the range [0, 4294967295].
     *
     * @return An unsigned integer in the range [0, 4294967295].
     */
    public fun readUInt(): UInt = nextInt<Unit>().toUInt()

    /**
     * Reads a long integer from the random source.
     * The value is normalized to the range [-9223372036854775808, 9223372036854775807].
     *
     * @return A long integer in the range [-9223372036854775808, 9223372036854775807].
     */
    public fun readLong(): Long = nextLong<Unit>()

    /**
     * Reads an unsigned long integer from the random source.
     * The value is normalized to the range [0, 18446744073709551615].
     *
     * @return An unsigned long integer in the range [0, 18446744073709551615].
     */
    public fun readULong(): ULong = nextLong<Unit>().toULong()

    /**
     * Reads a single-precision floating-point number from the random source.
     * The value is normalized to the range [0.0, 1.0) by dividing by 2^31.
     *
     * @return A single-precision floating-point number in the range [0.0, 1.0).
     */
    public fun readFloat(): Float = ((readInt() and 0x7fffffff) / (1 shl 31).toFloat()).absoluteValue

    /**
     * Reads a double-precision floating-point number from the random source.
     * The value is normalized to the range [0.0, 1.0) by dividing by 2^63.
     *
     * @return A double-precision floating-point number in the range [0.0, 1.0).
     */
    public fun readDouble(): Double = ((readLong() and 0x7fffffffffffffffL) / (1L shl 63).toDouble()).absoluteValue

    /**
     * Reads bytes into a data structure from the secure random source.
     * The data structure must provide a way to write bytes at specific indices.
     * This function allows for custom data structures to be filled with random bytes.
     *
     * @param data The data structure to fill with random bytes.
     * @param offset The starting index in the data structure to write to.
     * @param length The number of bytes to read. Defaults to 0, meaning the entire data structure will be filled.
     * @param writeOctet A function that writes a byte at a specific index in the data structure.
     */
    override fun <E> exportBytes(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
        require(length > 0) { "Zero length data" }

        repeat(length) { index ->
            data.writeOctet(index + offset, readByte())
        }
    }

    /**
     * Reads bytes into a ByteArray from the secure random source.
     *
     * @param data The ByteArray to fill with random bytes.
     * @param offset The starting index in the ByteArray to write to.
     * @param length The number of bytes to read. Defaults to the size of the ByteArray.
     */
    public fun readBytes(data: ByteArray, offset: Int = 0, length: Int = data.size) {
        exportBytes(data, offset, length) { index, value ->
            this[index] = value
        }
    }
}