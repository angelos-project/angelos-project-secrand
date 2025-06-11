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

import org.angproj.sec.util.floorMod


/**
 * AbstractSponge is an abstract class that implements a sponge construction
 * with a real custom size and visible size. It provides methods for absorbing,
 * squeezing, and scrambling the sponge state.
 *
 * @property spongeSize The total size of the sponge in long variables.
 * @property visibleSize The number of long variables that are visible for output.
 */
public abstract class AbstractSponge(
    override val spongeSize: Int = 0,
    override val visibleSize: Int = 0,
) : Sponge {
    protected var counter: Long = 0
    protected var mask: Long = 0
    protected val sponge: LongArray = LongArray(spongeSize)
    override val byteSize: Int = visibleSize * Long.SIZE_BYTES

    init {
        require(visibleSize <= spongeSize) {
            "Visible size must be equal or less than the number of sponge variables."
        }
        reset()
    }

    override fun reset() {
        repeat(spongeSize) {
            sponge[it] = InitializationVector.entries[it + 1].iv
        }
    }

    /**
     * The round function is an abstract method that must be implemented by subclasses.
     * It defines how the sponge state is transformed during each round of the sponge.
     */
    abstract override fun round()

    /**
     * Absorb a value into the sponge to a specific state.
     *
     * @param value The value to absorb.
     * @param position The position in the sponge to absorb the value.
     */
    override fun absorb(value: Long, position: Int) {
        val offset = position.floorMod(visibleSize)
        sponge[offset] = sponge[offset] xor value
    }

    /**
     * Squeeze a value from the sponge from a specific state.
     *
     * @param position The position in the sponge to squeeze the value from.
     * @return The squeezed value, which is a combination of the sponge state and a mask.
     */
    override fun squeeze(position: Int): Long {
        val offset = position.floorMod(visibleSize)
        return sponge[offset] xor (mask * export[offset])
    }

    /**
     * Scramble the sponge state by performing a number of rounds equal to the size of the sponge.
     */
    override fun scramble() {
        repeat(sponge.size) { round() }
    }

    protected companion object {
        protected val export: List<Int> = listOf(3, 7, 11, 19, 23, 31, 43, 47, 59, 67, 71, 79, 83, 103, 107, 127)
    }
}