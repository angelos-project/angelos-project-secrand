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
 * `AbstractSponge256` is an abstract class implementing a cryptographic sponge construction
 * with a linear state array of 4 x 64-bit registers. The state is conceptually interpreted as a 2x2 matrix,
 * enabling neighbor-based mixing operations for strong diffusion and confusion properties.
 * This design is suitable for secure random number generation, hashing, and other cryptographic primitives.
 *
 * ## Design Overview
 * - **State Representation:** The internal state is a linear array of 4 elements, treated as a 2x2 matrix.
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
public abstract class AbstractSponge256 : AbstractSponge(4, 4) {

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
        val d = sponge[0] xor sponge[3] // Diffusion between first and last element
        val r0 = sponge[0] xor sponge[2] // Diffusion between first and third element
        val r1 = sponge[1] xor sponge[3] // Diffusion between second and last element

        // Non-linear confusion transformations
        val temp = -sponge[3].inv() * 29
        val sponge3 = -sponge[2].inv() * 17
        val sponge2 = -sponge[1].inv() * 13
        val sponge1 = -sponge[0].inv() * 5
        val sponge0 = temp.rotateLeft(TypeSize.intBits) // Rotation for additional mixing

        // Update mask with a combination of state, counter, and intermediate values
        mask = (sponge0 and sponge2 and counter and mask) xor
                ((sponge1 and sponge3 and counter.inv()) * 2) xor
                ((r0 and r1) * 4) xor
                (d * 8)

        // Update state with new values, further mixed by XOR with diffusion results
        sponge[0] = sponge0 xor r0
        sponge[1] = sponge1 xor r0
        sponge[2] = sponge2 xor r1
        sponge[3] = sponge3 xor r1

        // Increment counter to ensure each round is unique
        counter++
    }
}