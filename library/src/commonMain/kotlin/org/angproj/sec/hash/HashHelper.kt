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

public class HashHelper(private val sponge: Sponge, position: Int = 0) {

    public enum class HashMode {
        SQUEEZING, ABSORBING
    }

    private var _position = position
    public val position: Int
        get() = _position

    private var _mode: HashMode = HashMode.ABSORBING
    public val mode: HashMode
        get() = _mode

    public fun forward() {
        _position++
        if(_position == sponge.visibleSize) {
            _position = 0
            sponge.round()
        }
    }

    public fun switchMode(): HashMode = when(_mode) {
        HashMode.SQUEEZING -> HashMode.ABSORBING
        HashMode.ABSORBING -> HashMode.SQUEEZING
    }.also { sponge.scramble() }
}