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

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Security
import org.angproj.sec.stat.Randomness
import org.angproj.sec.util.Octet
import org.angproj.sec.rand.RandomBits
import org.angproj.sec.util.ReadOctet
import org.angproj.sec.util.TypeSize
import kotlin.math.min


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
public class GarbageGarbler: Security(), RandomBits, Randomness {

    override val sponge: AbstractSponge1024 = object : AbstractSponge1024() {}

    private val entropy: ByteArray = ByteArray(128)
    private var entropyPos = 0

    /** Number of bytes missing to fill the entropy pool */
    public val missing: Int
        get() = entropy.size - entropyPos

    /** Number of bits readable from the garbler until depletion */
    public val remainingBits: Long
        get() = THRESHOLD - lastReseedBits

    /** Number of bytes readable from the garbler until depletion */
    public val remainingBytes: Int
        get() = (remainingBits / TypeSize.byteBits).toInt()

    init {
        // Seed the sponge
        JitterEntropy.readLongs(sponge, 0, sponge.visibleSize) { index, value ->
            sponge.absorb(value, index)
        }
        sponge.scramble()
    }

    override fun checkReseedConditions(): Boolean {
        return missing <= 0
    }

    override fun reseedImpl() {
        repeat(sponge.visibleSize) {
            val data = Octet.read(entropy, it * TypeSize.longSize, TypeSize.longSize) { index ->
                this[index]
            }
            sponge.absorb(data, it)
        }
        sponge.scramble()
        entropyPos = 0
    }

    override fun checkExportConditions(length: Int): Boolean {
        if(length <= remainingBytes) {
            return true
        }
        if(checkReseedConditions()) {
            reseed()
            return true
        }
        return false
    }

    /**
     * Attempts to reseed the internal entropy pool if the conditions are met.
     *
     * @return `true` if reseeding was performed, `false` otherwise.
     */
    public fun revitalize(): Boolean {
        if(checkReseedConditions()) {
            reseed()
            return true
        }
        return false
    }

    /**
     * Seeds bytes into the internal entropy pool.
     *
     * @param data The data source containing bytes to import.
     * @param offset The starting index in the data source.
     * @param length The number of bytes to import.
     * @param readOctet A lambda function to read a byte from the data source at a given index.
     * @throws IllegalArgumentException if length is less than or equal to zero.
     */
    public fun <E> seedEntropy(data: E, offset: Int, length: Int, readOctet: ReadOctet<E, Byte>) {
        require(length > 0) { "Zero length data" }

        repeat(min(length, missing)) { index ->
            entropy[entropyPos++] = data.readOctet(index + offset)
        }
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
        check(remainingBits >= bits) { "GarbageGarbler has depleted" }
        return sponge.getNextBits(bits)
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
        readBytes(data, offset, size) { index, value ->
            this[index] = value
        }
    }

    public companion object {
        private const val THRESHOLD: Long = Int.MAX_VALUE.toLong() / 2L * TypeSize.byteBits
    }
}