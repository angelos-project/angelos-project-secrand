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

/**
 * A utility class for absorbing data into a sponge function.
 * It manages the absorption process by interacting with the sponge and hash helper.
 *
 * @property sponge The sponge function used for absorption.
 * @property hashHelper The helper managing the hash state and mode.
 */
public class HashAbsorber(private val sponge: Sponge, private val hashHelper: HashHelper) {

    /**
     * Absorbs a single long value into the sponge.
     * This method advances the position in the sponge and performs a round if necessary.
     *
     * @param value The long value to absorb.
     * @throws IllegalStateException if the sponge is not in absorbing mode.
     */
    public fun absorb(value: Long) {
        check(hashHelper.mode == HashHelper.HashMode.ABSORBING) { "Sponge is not available" }
        sponge.absorb(value, hashHelper.position).also { hashHelper.forward() }
    }
}

/**
 * Creates an absorber function for the sponge starting at the specified position.
 * The returned function absorbs values from the slurp function and returns the current position.
 *
 * @param position The starting position for absorption (default is 0).
 * @param slurp A function that provides the next long value to absorb.
 * @return A function that absorbs a value and returns the current position.
 */
public fun Sponge.absorberOf(position: Int = 0, slurp: () -> Long): () -> Int {
    val helper = HashHelper(this, position, HashHelper.HashMode.ABSORBING)
    val absorber = HashAbsorber(this, helper)
    return {
        absorber.absorb(slurp())
        helper.position
    }
}