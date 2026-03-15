/**
 * Copyright (c) 2024-2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.rand.AbstractSecurity
import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.stat.Randomness
import org.angproj.sec.util.Octet
import org.angproj.sec.rand.RandomBits
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ceilDiv

/**
 * GarbageGarbler is a secure random number generator that implements RandomBits and Randomness.
 * It uses a 1024-bit sponge and provides methods for generating random values with health checks.
 */
public class GarbageGarbler: AbstractSecurity(object : AbstractSponge1024() {}), RandomBits, Randomness {

    /**
     * Indicates if the generator has been initialized.
     */
    public val isInitialized: Boolean
        get() = initialized

    /**
     * The number of remaining bytes that can be exported before reseeding.
     */
    public val remainingBytes: Int
        get() = ((Int.MAX_VALUE / 2L) - bytesExported - bitsExported.ceilDiv(TypeSize.byteBits.toLong())).toInt()

    /**
     * Reseeds the generator with entropy from the given source.
     *
     * @param seeder the entropy source.
     */
    public fun reseed(seeder: Octet.Producer) {
        seedEntropy(seeder)
    }

    override fun reseedPolicy(bytesNeeded: Int): Boolean = remainingBytes >= bytesNeeded

    /**
     * Generates the next random bits.
     *
     * @param bits the number of bits to generate.
     * @return the generated bits as an integer.
     */
    override fun nextBits(bits: Int): Int {
        require(bits in 1..TypeSize.intBits) { "Bits must be between 1 and 32" }
        check(reseedPolicy(bits.ceilDiv(TypeSize.byteBits))) { "Export conditions not met" }
        bitsExported += bits
        return RandomBits.compactBitEntropy(bits, hashSqueezer.squeeze())
    }

    /**
     * Reads a random byte.
     *
     * @return a random byte.
     */
    override fun readByte(): Byte = nextBits(TypeSize.byteBits).toByte()

    /**
     * Reads a random short.
     *
     * @return a random short.
     */
    override fun readShort(): Short = nextBits(TypeSize.shortBits).toShort()

    /**
     * Reads a random int.
     *
     * @return a random int.
     */
    override fun readInt(): Int = nextBits(TypeSize.intBits)

    /**
     * Reads random bytes into the array.
     *
     * @param data the byte array to fill.
     * @param offset the starting offset.
     * @param size the number of bytes to read.
     */
    override fun readBytes(data: ByteArray, offset: Int, size: Int) {
        exportBytes(data, offset, size) { index, value ->
            this[index] = value
        }
    }
}