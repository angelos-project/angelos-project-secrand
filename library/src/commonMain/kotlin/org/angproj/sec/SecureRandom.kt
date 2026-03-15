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
 * SecureRandom provides a simple interface for secure random values.
 * It delegates to SecureFeed for generation.
 */
public object SecureRandom : Randomness {

    /**
     * Reads a random byte.
     *
     * @return a random byte.
     */
    override fun readByte(): Byte = SecureFeed.nextBits(TypeSize.byteBits).toByte()

    /**
     * Reads a random short.
     *
     * @return a random short.
     */
    override fun readShort(): Short = SecureFeed.nextBits(TypeSize.shortBits).toShort()

    /**
     * Reads a random int.
     *
     * @return a random int.
     */
    override fun readInt(): Int = SecureFeed.nextBits(TypeSize.intBits)

    /**
     * Reads random bytes into the array.
     *
     * @param data the byte array to fill.
     * @param offset the starting offset.
     * @param size the number of bytes to read.
     */
    public override fun readBytes(data: ByteArray, offset: Int, size: Int) {
        SecureFeed.exportBytes(data, offset, size) { index, value ->
            this[index] = value
        }
    }
}