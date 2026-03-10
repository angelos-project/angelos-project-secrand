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


/**
 * GarbageGarbler is a secure random number generator that uses a sponge construction
 * to produce random bits. It maintains an internal entropy pool that can be
 * reseeded with external data. The generator can be depleted after a certain
 * amount of data has been read, at which point it will no longer produce random bits.
 *
 * The class implements the Randomizer and Randomness interfaces, providing methods
 * to read random bytes, shorts, ints, and to import additional entropy.
 *
 * @constructor Creates a new instance of GarbageGarbler with an initialized sponge and entropy pool.
 */
public class GarbageGarbler: AbstractSecurity(object : AbstractSponge1024() {}), RandomBits, Randomness {

    public val isInitialized: Boolean
        get() = initialized

    /** Number of bytes readable from the garbler until depletion */
    public val remainingBytes: Int
        get() = (Int.MAX_VALUE / 2) - (hashHelper.forwards.toInt() * 8)

    public fun reseed(seeder: Octet.Producer) {
        seedEntropy(seeder)
    }

    /**
     * Retrieves the next specified number of random bits from the sponge.
     *
     * @param bits The number of bits to retrieve (must be between 1 and 32).
     * @return An integer representing the next random bits.
     * @throws IllegalArgumentException if bits is not in the range 1 to 32.
     * @throws IllegalStateException if the GarbageGarbler is depleted.
     */
    override fun nextBits(bits: Int): Int {
        require(bits in 1..TypeSize.intBits) { "Bits must be between 1 and 32" }
        return RandomBits.compactBitEntropy(bits, hashSqueezer.squeeze())
    }

    override fun readByte(): Byte = nextBits(TypeSize.byteBits).toByte()

    override fun readShort(): Short = nextBits(TypeSize.shortBits).toShort()

    override fun readInt(): Int = nextBits(TypeSize.intBits)

    /**
     * Reads random bytes into a ByteArray from the GarbageGarbler.
     *
     * @param data The ByteArray to fill with random bytes.
     * @param offset The starting index in the ByteArray to write to.
     * @param size The number of bytes to read.
     * @throws IllegalStateException if the GarbageGarbler is depleted.
     */
    override fun readBytes(data: ByteArray, offset: Int, size: Int) {
        exportBytes(data, offset, size) { index, value ->
            this[index] = value
        }
    }
}