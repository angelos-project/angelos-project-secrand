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
 * It manages the hashing process by updating with data and producing a final hash.
 *
 * @param sponge the sponge instance used for hashing.
 */
public abstract class Hash(sponge: Sponge) {

    private val hash64 = Hash64(sponge)
    private var remainder: ByteArray = byteArrayOf()

    /**
     * Initializes the hash function, initializing the internal state.
     */
    public fun init() { hash64.init() }

    /**
     * Updates the hash with the provided data.
     *
     * @param data the byte array to hash.
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
     * Finalizes the hash computation and returns the hash value.
     *
     * @return the computed hash as a byte array.
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

    /**
     * Resets the hash function to its initial state.
     */
    public fun reset() { 
        hash64.reset() 
        remainder.fill(0)
    }
}