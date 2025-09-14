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

import org.angproj.sec.util.TypeSize

/**
 * `AbstractSponge512` is an abstract class implementing a cryptographic sponge construction
 * with a linear state array of 9 x 64-bit registers. The state is conceptually interpreted as a 3x3 matrix,
 * enabling neighbor-based and multi-element mixing operations for strong diffusion and confusion properties.
 * This design is suitable for secure random number generation, hashing, and other cryptographic primitives.
 *
 * ## Design Overview
 * - **State Representation:** The internal state is a linear array of 9 elements, treated as a 3x3 matrix.
 * - **Diffusion:** Achieved by mixing state elements using XOR and bitwise operations, spreading the influence of each bit.
 * - **Confusion:** Non-linear transformations (inversion, negation, multiplication, rotation) obscure relationships between input and output.
 * - **Permutation Round:** The `round` method combines diffusion, confusion, and round constants to update the state, ensuring unpredictability and preventing fixed points.
 *
 * ## Key Methods
 * - `round`: Executes a single permutation round, updating the state with diffusion, confusion, and round constants.
 *
 * ## Security Rationale
 * - **Diffusion** ensures that each bit of input affects many bits of output, making it hard to trace or reverse.
 * - **Confusion** introduces non-linearity, preventing attackers from predicting or reversing transformations.
 * - **Round Constants** and counter updates prevent fixed points and ensure each round is unique, thwarting certain attacks.
 *
 * ## Usage
 * This class is intended for cryptographic contexts requiring strong mixing and unpredictability.
 * Subclasses should implement additional logic for absorbing input and squeezing output as needed.
 *
 * ## Example
 * To use this sponge, extend the class and implement absorb/squeeze methods for your application.
 */
public abstract class AbstractSponge512 : AbstractSponge(9, 8) {

    /**
     * Executes a single permutation round on the sponge state.
     *
     * - Computes intermediate values using XOR between state elements for diffusion.
     * - Applies non-linear confusion using bitwise inversion, negation, multiplication, and rotation.
     * - Updates the mask with a combination of state, counter, and intermediate values.
     * - Updates the state with new values, further mixed by XOR with diffusion results.
     * - Increments the counter to ensure each round is unique.
     *
     * This process ensures strong mixing and unpredictability of the sponge state.
     */
    override fun round() {
        val d = sponge[0] xor sponge[4] xor sponge[8] // Diffusion across diagonal elements
        val r0 = sponge[0] xor sponge[3] xor sponge[6] // Row/column mixing
        val r1 = sponge[1] xor sponge[4] xor sponge[7]
        val r2 = sponge[2] xor sponge[5] xor sponge[8]

        // Non-linear confusion transformations
        val temp = -sponge[8].inv() * 73
        val sponge8 = -sponge[7].inv() * 61
        val sponge7 = -sponge[6].inv() * 53
        val sponge6 = -sponge[5].inv() * 41
        val sponge5 = -sponge[4].inv() * 37
        val sponge4 = -sponge[3].inv() * 29
        val sponge3 = -sponge[2].inv() * 17
        val sponge2 = -sponge[1].inv() * 13
        val sponge1 = -sponge[0].inv() * 5
        val sponge0 = temp.rotateLeft(TypeSize.intBits) // Rotation for additional mixing

        // Update mask with a combination of state, counter, and intermediate values
        mask = (mask and -counter.inv() and sponge0 and sponge3 and sponge6) xor
                ((sponge1 and sponge4 and sponge7 and sponge8) * 2) xor
                ((sponge2 and r2 and sponge5) * 4) xor
                ((r0 and r1) * 8) xor
                (d * 16)

        // Update state with new values, further mixed by XOR with diffusion results
        sponge[0] = sponge0 xor r0
        sponge[1] = sponge1 xor r0
        sponge[2] = sponge2 xor r0
        sponge[3] = sponge3 xor r1
        sponge[4] = sponge4 xor r1
        sponge[5] = sponge5 xor r1
        sponge[6] = sponge6 xor r2
        sponge[7] = sponge7 xor r2
        sponge[8] = sponge8 xor r2

        // Increment counter to ensure each round is unique
        counter++
    }
}