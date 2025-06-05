/**
 * Copyright (c) 2024-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.sec

import org.angproj.sec.util.ExportOctets

/**
 * *SecureRandom provides a high-quality source of random numbers
 * using a secure entropy feed. It reads bytes, shorts, ints, longs,
 * and floating-point numbers from the secure random source.
 * The values are normalized to their respective ranges.
 */
public object SecureRandom : ExportOctets {

    private val buffer = LongArray(128)
    private var position: Int = 0

    init {
        revitalize()
    }

    /**
     * Revitalizes the secure random source by reading from the secure feed.
     * This method is called when the internal buffer is exhausted.
     * It fills the buffer with new random data
     * and resets the position to zero.
     */
    private fun revitalize() {
        SecureFeed.read(buffer)
        position = 0
    }

    /**
     * Reads the next byte from the secure random source.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next byte as a Long value.
     */
    internal fun nextByte(): Long {
        if (position >= 1024) revitalize()
        val num = buffer[position / 8]
        return (num ushr 8 * (position++ % 8)) and 0xff
    }

    /**
     * Reads the next short integer from the secure random source.
     * It combines two bytes to form a short value.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next short as a Long value.
     */
    internal fun nextShort(): Long = (nextByte() or (nextByte() shl 8)) and 0xffff

    /**
     * Reads the next integer from the secure random source.
     * It combines four bytes to form an integer value.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next integer as a Long value.
     */
    internal fun nextInt(): Long = (nextShort() or (nextShort() shl 16)) and 0xffffffffL

    /**
     * Reads the next long integer from the secure random source.
     * It combines eight bytes to form a long value.
     * If the internal buffer is exhausted, it revitalizes the buffer.
     *
     * @return The next long as a Long value.
     */
    internal fun nextLong(): Long = (nextInt() or (nextInt() shl 32))


    /**
     * Reads a byte from the secure random source.
     * The value is normalized to the range [-128, 127].
     *
     * @return A byte in the range [-128, 127].
     */
    public fun readByte(): Byte = nextByte().toByte()

    /**
     * Reads an unsigned byte from the secure random source.
     * The value is normalized to the range [0, 255].
     *
     * @return An unsigned byte in the range [0, 255].
     */
    public fun readUByte(): UByte = nextByte().toUByte()

    /**
     * Reads a short integer from the secure random source.
     * The value is normalized to the range [-32768, 32767].
     *
     * @return A short integer in the range [-32768, 32767].
     */
    public fun readShort(): Short = nextShort().toShort()

    /**
     * Reads an unsigned short integer from the secure random source.
     * The value is normalized to the range [0, 65535].
     *
     * @return An unsigned short integer in the range [0, 65535].
     */
    public fun readUShort(): UShort = nextShort().toUShort()

    /**
     * Reads an integer from the secure random source.
     * The value is normalized to the range [-2147483648, 2147483647].
     *
     * @return An integer in the range [-2147483648, 2147483647].
     */
    public fun readInt(): Int = nextInt().toInt()

    /**
     * Reads an unsigned integer from the secure random source.
     * The value is normalized to the range [0, 4294967295].
     *
     * @return An unsigned integer in the range [0, 4294967295].
     */
    public fun readUInt(): UInt = nextInt().toUInt()

    /**
     * Reads a long integer from the secure random source.
     * The value is normalized to the range [-9223372036854775808, 9223372036854775807].
     *
     * @return A long integer in the range [-9223372036854775808, 9223372036854775807].
     */
    public fun readLong(): Long = nextLong()

    /**
     * Reads an unsigned long integer from the secure random source.
     * The value is normalized to the range [0, 18446744073709551615].
     *
     * @return An unsigned long integer in the range [0, 18446744073709551615].
     */
    public fun readULong(): ULong = nextLong().toULong()

    /**
     * Reads a single-precision floating-point number from the secure random source.
     * The value is normalized to the range [0.0, 1.0) by dividing by 2^31.
     *
     * @return A single-precision floating-point number in the range [0.0, 1.0).
     */
    public fun readFloat(): Float = (readInt() and 0x7fffffff) / (1 shl 31).toFloat()

    /**
     * Reads a double-precision floating-point number from the secure random source.
     * The value is normalized to the range [0.0, 1.0) by dividing by 2^63.
     *
     * @return A double-precision floating-point number in the range [0.0, 1.0).
     */
    public fun readDouble(): Double = (readLong() and 0x7fffffffffffffffL) / (1L shl 63).toDouble()

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
    override fun <E> export(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
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
        export(data, offset, length) { index, value ->
            this[index] = value
        }
    }
}