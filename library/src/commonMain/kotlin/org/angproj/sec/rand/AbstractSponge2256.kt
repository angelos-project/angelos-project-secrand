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
 * `AbstractSponge2256` is an abstract class implementing a cryptographic sponge construction
 * with a 2x2 state matrix (4 x 64-bit registers). It extends `AbstractSponge`, providing
 * specialized permutation logic for secure mixing of state, suitable for random number generation,
 * hashing, and other cryptographic applications.
 *
 * ## Design Overview
 * - **State Representation:** The sponge state is a linear array, conceptually a 2x2 matrix.
 * - **Diffusion:** Achieved by mixing each value with its matrix neighbors using bitwise shifts and XORs.
 * - **Confusion:** Non-linear transformation obscures input-output relationships, enhancing security.
 * - **Permutation Round:** Combines diffusion, confusion, and round constants to update the state.
 *
 * ## Key Methods
 * - `diffuse`: Mixes a value with its neighbors to spread input influence across the state.
 * - `confuse`: Applies non-linear operations to obscure relationships and resist cryptanalysis.
 * - `round`: Executes a single permutation round, updating the state with diffusion, confusion, and round constants.
 *
 * ## Security Rationale
 * - **Diffusion** ensures that each bit of input affects many bits of output, making it hard to trace.
 * - **Confusion** introduces non-linearity, preventing attackers from predicting or reversing transformations.
 * - **Round Constants** prevent fixed points and ensure each round is unique, thwarting certain attacks.
 *
 * ## Usage
 * This class is intended for cryptographic contexts where strong mixing and unpredictability are required.
 * Subclasses should implement additional logic for absorbing input and squeezing output as needed.
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