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

public class HashHelper(private val sponge: Sponge, position: Int = 0, mode: HashMode = HashMode.ABSORBING) {

    private val _absorber by lazy { HashAbsorber(sponge, this) }
    public val absorber: HashAbsorber
        get() = _absorber

    private val _squeezer by lazy { HashSqueezer(sponge, this) }
    public val squeezer: HashSqueezer
        get() = _squeezer

    public enum class HashMode {
        SQUEEZING, ABSORBING
    }

    private var _position = position
    public val position: Int
        get() = _position

    private var _mode: HashMode = mode
    public val mode: HashMode
        get() = _mode

    internal fun forward() {
        _position++
        if(_position == sponge.visibleSize) {
            _position = 0
            sponge.round()
        }
    }

    public fun reset(): Unit = reset(HashMode.ABSORBING)

    public fun reset(mode: HashMode) {
        sponge.reset()
        _mode = mode
        _position = 0
    }

    public fun switchMode(): HashMode = when(_mode) {
        HashMode.SQUEEZING -> HashMode.ABSORBING
        HashMode.ABSORBING -> HashMode.SQUEEZING
    }.also {
        sponge.scramble()
        _mode = it
        _position = 0
    }
}