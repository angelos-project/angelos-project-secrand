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
package org.angproj.sec.rand

/**
 * The `Sponge` interface defines the contract for a cryptographic sponge construction.
 * It provides methods for absorbing, squeezing, and scrambling data, as well as properties
 * describing the sponge's configuration.
 */
public interface Sponge {

    /**
     * The total number of states in the sponge.
     * */
    public val spongeSize: Int

    /**
     * The size of the visible part of the sponge state.
     * */
    public val visibleSize: Int

    /**
     * The size in bytes of the visible part of the sponge state.
     * */
    public val byteSize: Int

    /**
     * Resets states and the sponge to its original initialization vectors.
     * */
    public fun reset()

    /**
     * The round function is an abstract method that subclasses must implement.
     * It defines how the sponge state is transformed during each round of the permutation.
     */
    public fun round()

    /**
     * Absorb a value into the sponge at a specific offset, of the visible portion of the state.
     *
     * @param value The value to absorb.
     * @param position The position in the sponge to absorb the value.
     */
    public fun absorb(value: Long, position: Int)

    /**
     * Squeeze a value from the sponge from a specific offset, of the visible portion of the state.
     *
     * @param position The position in the sponge to squeeze the value from.
     * @return The squeezed value, which is a combination of the sponge state and a mask.
     */
    public fun squeeze(position: Int): Long

    /**
     * Scramble the sponge state by performing a number of rounds equal to the size of the sponge.
     */
    public fun scramble() {
        repeat(spongeSize) { round() }
    }
}