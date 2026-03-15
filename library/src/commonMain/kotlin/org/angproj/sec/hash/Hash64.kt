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
package org.angproj.sec.hash

import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.ReadOctet
import org.angproj.sec.util.RunState
import org.angproj.sec.util.WriteOctet

/**
 * A 64-bit hash implementation using a sponge function.
 * This class provides methods to initialize, update with data, finalize the hash, and reset the state.
 * It operates on long values and produces a hash of the sponge's visible size.
 *
 * @property sponge The underlying sponge function for hashing.
 */
public class Hash64(private val sponge: Sponge) {

    /**
     * The bit size of the sponge.
     */
    public val bitSize: Int
        get() = sponge.bitSize

    /**
     * The byte size of the sponge.
     */
    public val byteSize: Int
        get() = sponge.byteSize

    /**
     * The visible size of the sponge, indicating the output hash length.
     */
    public val visibleSize: Int
        get() = sponge.visibleSize

    private val hashHelper = HashHelper(sponge)

    private var state = RunState.INITIALIZE

    /**
     * Initializes the hash computation.
     * Must be called before updating or finalizing.
     *
     * @throws IllegalStateException if already initialized.
     */
    public fun init() {
        check(state == RunState.INITIALIZE)
        state = RunState.RUNNING
    }

    /**
     * Updates the hash with data from the source.
     * Absorbs the specified number of elements starting from the offset.
     *
     * @param E The type of the source data.
     * @param src The source data to absorb.
     * @param offset The starting offset in the source.
     * @param size The number of elements to absorb.
     * @param readOctet A function to read a long from the source at a given index.
     * @throws IllegalStateException if not in running state.
     */
    public fun<E> update(src: E, offset: Int, size: Int, readOctet: ReadOctet<E, Long>) {
        check(state == RunState.RUNNING)

        val absorber = hashHelper.absorber
        repeat(size) {
            absorber.absorb(src.readOctet(offset+it))
        }
    }

    /**
     * Finalizes the hash computation and writes the result to the destination.
     * The output size must match the sponge's visible size.
     *
     * @param E The type of the destination.
     * @param dst The destination to write the hash to.
     * @param offset The starting offset in the destination.
     * @param size The number of elements to write (must equal visibleSize).
     * @param writeOctet A function to write a long to the destination at a given index.
     * @throws IllegalArgumentException if size does not match visibleSize.
     * @throws IllegalStateException if not in running state.
     */
    public fun<E> final(dst: E, offset: Int, size: Int, writeOctet: WriteOctet<E, Long>) {
        require(size == sponge.visibleSize)
        check(state == RunState.RUNNING)

        state = RunState.FINISHED
        hashHelper.switchMode()

        val squeezer = hashHelper.squeezer
        repeat(sponge.visibleSize) {
            dst.writeOctet(offset+it, squeezer.squeeze())
        }
    }

    /**
     * Resets the hash to its initial state, allowing reuse.
     */
    public fun reset() {
        sponge.reset()
        state = RunState.INITIALIZE
    }
}