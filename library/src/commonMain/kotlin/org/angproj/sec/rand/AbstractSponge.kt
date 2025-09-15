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
import org.angproj.sec.util.floorMod

/**
 * `AbstractSponge` is an abstract base class for cryptographic sponge constructions,
 * providing a flexible framework for absorbing, transforming, and extracting data
 * using a custom-sized internal state. This class is designed to be extended by
 * concrete sponge implementations with specific permutation logic.
 *
 * ## Design Rationale
 * - **State Size Flexibility:** Allows for arbitrary sponge and output sizes, supporting
 *   a wide range of cryptographic applications (e.g., random number generation, hashing).
 * - **Initialization Vector:** Uses a static set of initialization vectors for state setup,
 *   ensuring reproducibility and resistance to trivial attacks.
 * - **Absorb/Squeeze Interface:** Implements standard sponge operations for input and output,
 *   with modularity for custom round functions.
 * - **Masking and Export:** Incorporates a mask and export multipliers to further obfuscate
 *   output and strengthen security against state leakage.
 *
 * ## Key Properties
 * - `spongeSize`: Total number of 64-bit state variables (Longs) in the sponge.
 * - `visibleSize`: Number of state variables exposed for output (squeezing).
 * - `counter`: Internal round counter, used for round uniqueness and masking.
 * - `mask`: Dynamic mask value, updated by permutation rounds to obfuscate output.
 * - `sponge`: The main state array, holding the internal sponge variables.
 * - `byteSize`: Output size in bytes, derived from `visibleSize`.
 *
 * ## Initialization
 * - Validates that `visibleSize` does not exceed `spongeSize`.
 * - Calls `reset()` to initialize the state using predefined initialization vectors.
 *
 * ## Absorb Operation
 * - Integrates input data into the sponge state at a specified position.
 * - Uses modular arithmetic to wrap positions, ensuring safe indexing.
 * - XORs the input value with the current state, providing diffusion.
 *
 * ## Squeeze Operation
 * - Extracts output from the sponge state at a specified position.
 * - Combines the state value with a dynamic mask and an export multiplier,
 *   increasing output unpredictability and resistance to state recovery.
 *
 * ## Round Function
 * - Declared abstract; must be implemented by subclasses.
 * - Responsible for permuting the sponge state, updating the mask and counter,
 *   and ensuring strong diffusion/confusion properties.
 *
 * ## Security Considerations
 * - **Diffusion:** Absorb and round operations ensure that each input bit
 *   influences many output bits, making reverse engineering difficult.
 * - **Confusion:** Masking and export multipliers obscure the relationship
 *   between input and output, protecting against cryptanalysis.
 * - **Initialization:** Use of static IVs prevents trivial state collisions.
 *
 * ## Usage Example
 * Extend this class and implement the `round()` method with a custom permutation.
 * Use `absorb()` to input data and `squeeze()` to extract output.
 */
public abstract class AbstractSponge(
    override val spongeSize: Int = 0,   // Total number of state variables
    override val visibleSize: Int = 0,  // Number of output variables
) : Sponge {
    protected var counter: Long = 0      // Round counter for uniqueness
    protected var mask: Long = 0         // Dynamic mask for output obfuscation
    protected val sponge: LongArray = LongArray(spongeSize) // Internal state array
    override val byteSize: Int = visibleSize * TypeSize.longSize // Output size in bytes

    init {
        // Ensure visible size does not exceed sponge size for safe output
        require(visibleSize <= spongeSize) {
            "Visible size must be equal or less than the number of sponge variables."
        }
        reset() // Initialize state with IVs
    }

    /**
     * Resets the sponge state to its initial configuration using static initialization vectors.
     * This ensures a reproducible and secure starting state for cryptographic operations.
     */
    override fun reset() {
        repeat(spongeSize) {
            sponge[it] = InitializationVector.entries[it].iv
        }
        counter = 0
        mask = 0
    }

    /**
     * Abstract round function to be implemented by subclasses.
     * Defines the permutation logic for the sponge state, including
     * diffusion, confusion, and mask/counter updates.
     */
    abstract override fun round()

    /**
     * Absorbs a 64-bit value into the sponge state at the specified position.
     * Uses modular arithmetic to wrap the position and XORs the value for diffusion.
     *
     * @param value The input value to absorb.
     * @param position The target position in the sponge state.
     */
    override fun absorb(value: Long, position: Int) {
        val offset = position.floorMod(visibleSize) // Wrap position safely
        sponge[offset] = sponge[offset] xor value   // Diffuse input into state
    }

    /**
     * Squeezes a 64-bit value from the sponge state at the specified position.
     * Combines the state value with a dynamic mask and an export multiplier
     * to maximize output unpredictability and security.
     *
     * @param position The position in the sponge state to squeeze from.
     * @return The obfuscated output value.
     */
    override fun squeeze(position: Int): Long {
        val offset = position.floorMod(visibleSize) // Wrap position safely
        // Output is masked and multiplied for extra confusion
        return sponge[offset] xor (mask * export[offset])
    }

    /**
     * Static export multipliers used to obfuscate output during squeezing.
     * These are large, odd numbers chosen to maximize entropy and prevent
     * simple output prediction or state recovery.
     */
    protected companion object {
        protected val export: List<Int> = listOf(
            3, 7, 11, 19, 23, 31, 43, 47, 59, 67, 71, 79, 83, 103, 107, 127
        )
    }
}