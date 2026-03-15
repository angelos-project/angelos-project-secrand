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
package org.angproj.sec.rand

import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.floorMod
import kotlin.jvm.JvmStatic

/**
 * Abstract base class for sponge-based cryptographic constructions.
 * It manages the sponge state, including absorption, squeezing, and rounds, providing a foundation for various sponge implementations.
 *
 * @param spongeSize the total number of state variables in the sponge.
 * @param visibleSize the number of output variables available for squeezing.
 */
public abstract class AbstractSponge(
    override val spongeSize: Int = 0,   // Total number of state variables
    override val visibleSize: Int = 0,  // Number of output variables
) : Sponge {
    protected var counter: Long = 0      // Round counter for uniqueness
    protected var mask: Long = 0         // Dynamic mask for output obfuscation
    protected val sponge: LongArray = LongArray(spongeSize) // Internal state array
    override val byteSize: Int = visibleSize * TypeSize.longSize // Output size in bytes
    override val bitSize: Int = visibleSize * TypeSize.longBits // Output size in bytes

    init {
        // Ensure visible size does not exceed sponge size for safe output
        require(visibleSize <= spongeSize) {
            "Visible size must be equal or less than the number of sponge variables."
        }
        reset() // Initialize state with IVs
    }

    /**
     * Resets the sponge to its initial state, initializing the sponge array with initialization vectors.
     */
    override fun reset() {
        repeat(spongeSize) {
            sponge[it] = InitializationVector.entries[it].iv
        }
        counter = 0
        mask = 0
    }

    /**
     * Performs one round of the sponge permutation, updating the state and mask.
     */
    abstract override fun round()

    /**
     * Absorbs a value into the sponge at the specified position, diffusing it into the state.
     *
     * @param value the value to absorb.
     * @param position the position in the visible size to absorb into.
     */
    override fun absorb(value: Long, position: Int) {
        val offset = position.floorMod(visibleSize) // Wrap position safely
        sponge[offset] = sponge[offset] xor value   // Diffuse input into state
    }

    /**
     * Squeezes a value from the sponge at the specified position, applying masking for obfuscation.
     *
     * @param position the position in the visible size to squeeze from.
     * @return the squeezed value, masked for additional security.
     */
    override fun squeeze(position: Int): Long {
        val offset = position.floorMod(visibleSize) // Wrap position safely
        // Output is masked and multiplied for extra confusion
        return sponge[offset] xor (mask * export[offset])
    }
    
    protected companion object {
        @JvmStatic
        protected val export: List<Int> = listOf(
            3, 7, 11, 19, 23, 31, 43, 47, 59, 67, 71, 79, 83, 103, 107, 127
        )
    }
}