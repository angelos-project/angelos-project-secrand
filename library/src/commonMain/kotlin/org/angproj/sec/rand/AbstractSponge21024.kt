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
 * `AbstractSponge21024` is an abstract class implementing a custom cryptographic sponge construction
 * with a linear state array of 16 x 64-bit registers. The state is conceptually interpreted as a 4x4 matrix,
 * enabling neighbor-based mixing operations for strong diffusion and confusion properties.
 * This design is suitable for secure random number generation, hashing, and other cryptographic primitives.
 *
 * ## Design Overview
 * - **State Representation:** The internal state is a linear array of 16 elements, treated as a 4x4 matrix.
 * - **Diffusion:** The `diffuse` method mixes each value with its matrix neighbors using bitwise shifts and XORs,
 *   spreading the influence of each bit across the state.
 * - **Confusion:** The `confuse` method applies non-linear transformations to obscure relationships between input and output,
 *   enhancing resistance to cryptanalysis.
 * - **Permutation Round:** The `round` method combines diffusion, confusion, and round constants to update the state,
 *   ensuring unpredictability and preventing fixed points.
 *
 * ## Key Methods
 * - `diffuse`: Mixes a value with its up, down, right, and left neighbors to achieve diffusion.
 * - `confuse`: Applies non-linear operations (inversion, negation, multiplication, XOR) for cryptographic confusion.
 * - `round`: Executes a single permutation round, updating the state with diffusion, confusion, and round constants.
 *
 * ## Security Rationale
 * - **Diffusion** ensures that each bit of input affects many bits of output, making it hard to trace or reverse.
 * - **Confusion** introduces non-linearity, preventing attackers from predicting or reversing transformations.
 * - **Round Constants** prevent fixed points and ensure each round is unique, thwarting certain attacks.
 *
 * ## Usage
 * This class is intended for cryptographic contexts requiring strong mixing and unpredictability.
 * Subclasses should implement additional logic for absorbing input and squeezing output as needed.
 */
public abstract class AbstractSponge21024:  AbstractSponge(16, 16) {

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
        val sponge0 = diffuse<Unit>(sponge[0], 4, 12, 1, 3)
        val sponge1 = diffuse<Unit>(sponge[1], 5, 13, 2, 0)
        val sponge2 = diffuse<Unit>(sponge[2], 6, 14, 3, 1)
        val sponge3 = diffuse<Unit>(sponge[3], 7, 15, 0, 2)
        val sponge4 = diffuse<Unit>(sponge[4], 8, 0, 5, 7)
        val sponge5 = diffuse<Unit>(sponge[5], 9, 1, 6, 4)
        val sponge6 = diffuse<Unit>(sponge[6], 10, 2, 7, 5)
        val sponge7 = diffuse<Unit>(sponge[7], 11, 3, 4, 6)
        val sponge8 = diffuse<Unit>(sponge[8], 12, 4, 9, 11)
        val sponge9 = diffuse<Unit>(sponge[9], 13, 5, 10, 8)
        val sponge10 = diffuse<Unit>(sponge[10], 14, 6, 11, 9)
        val sponge11 = diffuse<Unit>(sponge[11], 15, 7, 8, 10)
        val sponge12 = diffuse<Unit>(sponge[12], 0, 8, 13, 15)
        val sponge13 = diffuse<Unit>(sponge[13], 1, 9, 14, 12)
        val sponge14 = diffuse<Unit>(sponge[14], 2, 10, 15, 13)
        val sponge15 = diffuse<Unit>(sponge[15], 3, 11, 12, 14)

        mask = (mask and -counter.inv() and sponge1 and sponge2 and sponge3) xor
                ((sponge4 and sponge5 and sponge6 and sponge7) * 2) xor
                ((sponge8 and sponge9 and sponge10) * 4) xor
                ((sponge11 and sponge12) * 8) xor
                (sponge13 * 16)

        sponge[0] = confuse<Unit>(sponge0)
        sponge[1] = confuse<Unit>(sponge1)
        sponge[2] = confuse<Unit>(sponge2)
        sponge[3] = confuse<Unit>(sponge3)
        sponge[4] = confuse<Unit>(sponge4)
        sponge[5] = confuse<Unit>(sponge5)
        sponge[6] = confuse<Unit>(sponge6)
        sponge[7] = confuse<Unit>(sponge7)
        sponge[8] = confuse<Unit>(sponge8)
        sponge[9] = confuse<Unit>(sponge9)
        sponge[10] = confuse<Unit>(sponge10)
        sponge[11] = confuse<Unit>(sponge11)
        sponge[12] = confuse<Unit>(sponge12)
        sponge[13] = confuse<Unit>(sponge13)
        sponge[14] = confuse<Unit>(sponge14)
        sponge[15] = confuse<Unit>(sponge15)

        sponge[0] = sponge[0] xor (-0x5a5a5a5a5a5a5a5bL xor counter++)
    }
}