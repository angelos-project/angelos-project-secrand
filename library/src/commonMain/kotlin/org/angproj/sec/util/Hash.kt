/**
 * Copyright (c) 2025-2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.hash.Hash64
import org.angproj.sec.rand.Sponge


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
public abstract class Hash(sponge: Sponge) {

    private val hash64 = Hash64(sponge)
    private var remainder: ByteArray = byteArrayOf()

    public fun init() { hash64.init()}

    /**
     * Absorbs the input ByteArray into the sponge state, treating the input as big endian.
     * On a little endian system, bytes are reversed before conversion to Long.
     */
    public fun update(data: ByteArray) {
        val input = remainder + data
        hash64.update(input, 0, input.size.floorDiv(TypeSize.longSize)) {
            Octet.readNet(data, it * TypeSize.longSize, TypeSize.longSize) { index ->
                data[index]
            }
        }
        remainder = input.copyOfRange(input.size - input.size.floorMod(TypeSize.longSize), input.size)
    }

    /**
     * Returns the digest as a ByteArray in big endian order.
     * Converts each internal little endian Long to big endian bytes.
     */
    public fun final(): ByteArray {
        if(remainder.isNotEmpty()) {
            update(remainder + ByteArray(TypeSize.longSize - remainder.size))
            remainder = byteArrayOf()
        }

        val output = ByteArray(hash64.byteSize)
        hash64.final(output, 0, hash64.visibleSize) { index, long ->
            Octet.writeNet(long, output, index * TypeSize.longSize, TypeSize.longSize) { idx, byte ->
                this[idx] = byte
            }
        }
        return output
    }
}