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
 * Abstract sponge implementation with a 256-bit state and 256-bit output, using a different permutation.
 * It provides a specific round function with diffusion and confusion operations.
 */
public abstract class AbstractSponge2256:  AbstractSponge(4, 4) {

    private inline fun<reified R: Any> diffuse(value: Long, up: Int, down: Int, right: Int, left: Int): Long {
        return value xor
                (sponge[up] ushr 3) xor // Rotate right by 3
                (sponge[down] shl 5) xor // Rotate left by 5
                (sponge[right] ushr 7) xor // Rotate right by 7
                (sponge[left] shl 11) // Rotate left by 11
    }

    private inline fun<reified R: Any> confuse(value: Long): Long {
        return value xor (-value.inv() * 11) xor (-value.inv() * 7)
    }

    /**
     * Performs the round function for the 256-bit sponge, applying diffusion and confusion.
     */
    override fun round() {
        val sponge0 = diffuse<Unit>(sponge[0], 2, 2, 1, 1)
        val sponge1 = diffuse<Unit>(sponge[1], 3, 3, 0, 0)
        val sponge2 = diffuse<Unit>(sponge[2], 0, 0, 3, 3)
        val sponge3 = diffuse<Unit>(sponge[3], 1, 1, 2, 2)

        mask = (mask and -counter.inv() and sponge0 and sponge1) xor
                ((sponge2 and sponge3 and counter) * 2) xor
                ((sponge[0] and sponge[1]) * 4) xor
                (sponge[2] * 8)

        sponge[0] = confuse<Unit>(sponge0)
        sponge[1] = confuse<Unit>(sponge1)
        sponge[2] = confuse<Unit>(sponge2)
        sponge[3] = confuse<Unit>(sponge3)

        sponge[0] = sponge[0] xor (-0x5a5a5a5a5a5a5a5bL xor counter++)
    }
}