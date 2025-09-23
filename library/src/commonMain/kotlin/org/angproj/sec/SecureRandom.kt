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

import org.angproj.sec.stat.Randomness
import org.angproj.sec.util.TypeSize

/**
 * SecureRandom provides a high-quality source of random numbers
 * using a secure entropy feed. It reads bytes, shorts, ints, longs,
 * and floating-point numbers from the secure random source.
 */
public object SecureRandom : Randomness {

    override fun readByte(): Byte = SecureFeed.getNextBits(TypeSize.byteBits).toByte()

    override fun readShort(): Short = SecureFeed.getNextBits(TypeSize.shortBits).toShort()

    override fun readInt(): Int = SecureFeed.getNextBits(TypeSize.intBits)

    /**
     * Reads bytes into a ByteArray from the secure random source.
     *
     * @param data The ByteArray to fill with random bytes.
     * @param offset The starting index in the ByteArray to write to.
     * @param size The number of bytes to read. Defaults to the size of the ByteArray.
     */
    public override fun readBytes(data: ByteArray, offset: Int, size: Int) {
        SecureFeed.exportBytes(data, offset, size) { index, value ->
            this[index] = value
        }
    }
}