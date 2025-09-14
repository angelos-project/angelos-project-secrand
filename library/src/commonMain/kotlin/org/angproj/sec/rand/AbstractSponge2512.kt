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
 * Custom cryptographic sponge with a linear 16-state array of 64-bit registers,
 * interpreted as a 4x4 matrix for operations.
 * Deterministic initialization, single-round permutation for absorb/squeeze, 16 rounds for scramble.
 */
public abstract class AbstractSponge2512:  AbstractSponge(9, 8) {

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
     * Single round of permutation, combining linear and non-linear transformations.
     * Treats the linear state as a 4x4 matrix for neighbor operations.
     */
    override fun round() {
        // Step 1: Linear mixing (matrix-like diffusion)
        val sponge0 = diffuse<Unit>(sponge[0], 3, 6, 1, 2)
        val sponge1 = diffuse<Unit>(sponge[1], 4, 7, 2, 0)
        val sponge2 = diffuse<Unit>(sponge[2], 5, 8, 0, 1)
        val sponge3 = diffuse<Unit>(sponge[3], 6, 0, 4, 5)
        val sponge4 = diffuse<Unit>(sponge[4], 7, 1, 5, 3)
        val sponge5 = diffuse<Unit>(sponge[5], 8, 2, 3, 4)
        val sponge6 = diffuse<Unit>(sponge[6], 0, 3, 7, 8)
        val sponge7 = diffuse<Unit>(sponge[7], 1, 4, 8, 6)
        val sponge8 = diffuse<Unit>(sponge[8], 2, 5, 6, 7)

        mask = (mask and -counter.inv() and sponge1 and sponge2) xor
                ((sponge3 and sponge4 and sponge5) * 2) xor
                ((sponge6 and sponge7) * 4) xor
                (sponge8 * 8)

        sponge[0] = confuse<Unit>(sponge0)
        sponge[1] = confuse<Unit>(sponge1)
        sponge[2] = confuse<Unit>(sponge2)
        sponge[3] = confuse<Unit>(sponge3)
        sponge[4] = confuse<Unit>(sponge4)
        sponge[5] = confuse<Unit>(sponge5)
        sponge[6] = confuse<Unit>(sponge6)
        sponge[7] = confuse<Unit>(sponge7)
        sponge[8] = confuse<Unit>(sponge8)

        // Step 3: Add round constant to prevent fixed points
        sponge[0] = sponge[0] xor (-0x5a5a5a5a5a5a5a5bL xor counter++)
    }
}