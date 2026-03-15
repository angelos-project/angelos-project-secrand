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
 * A helper class for managing the state of hash operations in a sponge function.
 * It tracks the current position, mode (absorbing or squeezing), and provides access to absorber and squeezer instances.
 *
 * @property sponge The sponge function being managed.
 * @param position The initial position (default is 0).
 * @param mode The initial mode (default is ABSORBING).
 */
public class HashHelper(private val sponge: Sponge, position: Int = 0, mode: HashMode = HashMode.ABSORBING) {

    private val _absorber by lazy { HashAbsorber(sponge, this) }

    /**
     * The absorber instance for absorbing data into the sponge.
     */
    public val absorber: HashAbsorber
        get() = _absorber

    private val _squeezer by lazy { HashSqueezer(sponge, this) }

    /**
     * The squeezer instance for squeezing hash values from the sponge.
     */
    public val squeezer: HashSqueezer
        get() = _squeezer

    /**
     * Enumeration representing the modes of hash operation.
     */
    public enum class HashMode {
        SQUEEZING, ABSORBING
    }

    private var _forwards = 0L

    /**
     * The number of forward operations performed.
     */
    public val forwards: Long
        get() = _forwards

    private var _position = position

    /**
     * The current position in the sponge.
     */
    public val position: Int
        get() = _position

    private var _mode: HashMode = mode

    /**
     * The current mode of operation (absorbing or squeezing).
     */
    public val mode: HashMode
        get() = _mode

    internal fun forward() {
        _forwards++
        _position++
        if(_position == sponge.visibleSize) {
            _position = 0
            sponge.round()
        }
    }
    
    /**
     * Resets the helper to the default absorbing mode.
     */
    public fun reset(): Unit = reset(HashMode.ABSORBING)

    /**
     * Resets the helper to the specified mode.
     *
     * @param mode The mode to reset to.
     */
    public fun reset(mode: HashMode) {
        sponge.reset()
        _mode = mode
        _position = 0
        _forwards = 0
    }

    /**
     * Switches the mode between absorbing and squeezing.
     * If the current position is not zero, performs a round and scrambles the sponge.
     *
     * @return The new mode after switching.
     */
    public fun switchMode(): HashMode = when(_mode) {
        HashMode.SQUEEZING -> HashMode.ABSORBING
        HashMode.ABSORBING -> HashMode.SQUEEZING
    }.also {
        if(_position != 0) sponge.round()
        sponge.scramble()
        _mode = it
        _position = 0
    }
}