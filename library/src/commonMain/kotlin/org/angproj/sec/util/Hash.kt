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

import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.Octet.importBytes

/**
 * Abstract base class for hash functions using a sponge construction.
 *
 * This class provides the common functionality for hashing data using a sponge-based
 * approach. It manages the absorption of input data, handling of remainders, and
 * finalization of the hash output. Subclasses must provide a specific sponge implementation.
 *
 * Used for testing purposes.
 *
 * @param E The type of sponge used in the hash function, must implement the Sponge interface.
 * @property sponge The sponge instance used for hashing.
 */
public abstract class Hash<E: Sponge>(sponge: E) {

    private var state = RunState.INITIALIZE
    private val sponge: E = sponge
    private var remainder: ByteArray = byteArrayOf()
    private var offset = 0

    public fun init() {
        check(state == RunState.INITIALIZE)
        state = RunState.RUNNING
    }

    /**
     * Absorbs the input ByteArray into the sponge state, treating the input as big endian.
     * On a little endian system, bytes are reversed before conversion to Long.
     */
    public fun update(input: ByteArray) {
        check(state == RunState.RUNNING)
        runUpdate(input)
    }

    public fun<E> update(src: E, offset: Int, size: Int, readOctet: ReadOctet<E, Byte>) {
        check(state == RunState.RUNNING)
        val input = ByteArray(size) { src.readOctet(offset + it) }
        runUpdate(input)
    }

    private fun runUpdate(input: ByteArray) {
        val data = remainder + input
        val loops = (data.size - data.size.mod(TypeSize.longSize)).div(TypeSize.longSize)
        repeat(loops){
            val value = Octet.readLE(data, it * TypeSize.longSize, TypeSize.longSize) { index ->
                data[index]
            }
            sponge.absorb(value, offset++ % sponge.visibleSize)
            if(offset == sponge.visibleSize) {
                offset = 0
                sponge.round()
            }
        }
        remainder = data.copyOfRange(data.size - (data.size.mod(TypeSize.longSize)), data.size)
    }

    private fun runFinalize() {
        if(remainder.isNotEmpty()) {
            runUpdate(ByteArray(TypeSize.longSize - remainder.size.mod(TypeSize.longSize)))
            check(remainder.isEmpty()) { "Remainder must be empty, still " + remainder.size + " bytes left" }
        }
        if(offset != 0) {
            sponge.round()
        }
        sponge.scramble()
    }

    /**
     * Returns the digest as a ByteArray in big endian order.
     * Converts each internal little endian Long to big endian bytes.
     */
    public fun final(): ByteArray = ByteArray(sponge.byteSize).also { it.importBytes(::final) }

    public fun<E> final(dst: E, offset: Int, size: Int, writeOctet: WriteOctet<E, Byte>) {
        check(state != RunState.INITIALIZE)
        check(size == sponge.byteSize)

        if (state == RunState.RUNNING) {
            state = RunState.FINISHED
            runFinalize()
        }

        repeat(sponge.visibleSize) {
            Octet.writeLE(
                sponge.squeeze(it),
                dst,
                offset + it * TypeSize.longSize,
                TypeSize.longSize,
                writeOctet
            )
        }
    }
}