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
 * Interface representing a cryptographic sponge construction.
 *
 * A sponge construction is a flexible cryptographic primitive used for hashing, random number generation,
 * and authenticated encryption. It operates by absorbing input data into an internal state, applying
 * a permutation (round function), and squeezing output data from the state.
 *
 * Properties:
 * - `spongeSize`: Total number of elements in the internal state.
 * - `visibleSize`: Number of elements in the state that are accessible for input/output operations.
 * - `byteSize`: Size in bytes of the visible portion of the state.
 *
 * Methods:
 * - `reset()`: Restores the sponge to its initial state.
 * - `round()`: Applies the permutation to the internal state (must be implemented by subclasses).
 * - `absorb(value, position)`: Incorporates a value into the visible part of the state at the given position.
 * - `squeeze(position)`: Extracts a value from the visible part of the state at the given position.
 * - `scramble()`: Applies the round function `spongeSize` times to thoroughly mix the state.
 */
public interface Sponge {

    /**
     * Total number of elements in the sponge's internal state.
     */
    public val spongeSize: Int

    /**
     * Number of elements in the visible portion of the state.
     */
    public val visibleSize: Int

    /**
     * Size in bytes of the visible portion of the state.
     */
    public val byteSize: Int

    /**
     * Resets the sponge to its initial state.
     */
    public fun reset()

    /**
     * Applies the permutation to the internal state.
     * Must be implemented by subclasses.
     */
    public fun round()

    /**
     * Absorbs a value into the visible part of the state at the specified position.
     *
     * @param value Value to absorb.
     * @param position Index in the visible state to absorb the value.
     */
    public fun absorb(value: Long, position: Int)

    /**
     * Squeezes a value from the visible part of the state at the specified position.
     *
     * @param position Index in the visible state to squeeze the value from.
     * @return Extracted value.
     */
    public fun squeeze(position: Int): Long

    /**
     * Applies the round function `spongeSize` times to scramble the state.
     */
    public fun scramble() {
        repeat(spongeSize) { round() }
    }
}