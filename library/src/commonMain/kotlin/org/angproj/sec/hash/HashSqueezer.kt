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
 * A utility class for squeezing hash values from a sponge function.
 * It manages the squeezing process by interacting with the sponge and hash helper.
 *
 * @property sponge The sponge function used for squeezing.
 * @property hashHelper The helper managing the hash state and mode.
 */
public class HashSqueezer(public val sponge: Sponge, private val hashHelper: HashHelper) {
    /**
     * Squeezes a single long value from the sponge.
     * This method advances the position in the sponge and performs a round if necessary.
     *
     * @return The squeezed long value.
     * @throws IllegalStateException if the sponge is not in squeezing mode.
     */
    public fun squeeze(): Long {
        check(hashHelper.mode == HashHelper.HashMode.SQUEEZING) { "Sponge is not available" }
        return sponge.squeeze(hashHelper.position).also { hashHelper.forward() }
    }
}

/**
 * Creates a squeezer function for the sponge starting at the specified position.
 * The returned function can be called repeatedly to squeeze values.
 *
 * @param position The starting position for squeezing (default is 0).
 * @return A function that squeezes a long value each time it's called.
 */
public fun Sponge.squeezerOf(position: Int = 0): () -> Long {
    val helper = HashHelper(this, position, HashHelper.HashMode.SQUEEZING)
    val squeezer = HashSqueezer(this, helper)
    return { squeezer.squeeze() }
}