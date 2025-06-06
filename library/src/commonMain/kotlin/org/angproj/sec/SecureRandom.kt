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
package org.angproj.sec

import org.angproj.sec.rand.AbstractRandom

/**
 * *SecureRandom provides a high-quality source of random numbers
 * using a secure entropy feed. It reads bytes, shorts, ints, longs,
 * and floating-point numbers from the secure random source.
 * The values are normalized to their respective ranges.
 */
public object SecureRandom : AbstractRandom() {

    init {
        refill()
    }

    /**
     * Revitalizes the secure random source by reading from the secure feed.
     * This method is called when the internal buffer is exhausted.
     * It fills the buffer with new random data
     * and resets the position to zero.
     */
    override fun refill() {
        SecureFeed.exportLongs(buffer, 0, buffer.size) { index, value ->
            buffer[index] = value
        }
    }
}