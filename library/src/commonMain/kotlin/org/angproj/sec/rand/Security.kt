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

internal interface Security : Sponge, Randomizer {
    public var position: Int

    override fun getNextBits(bits: Int): Int {
        val random = squeeze(position++)

        if(position >= visibleSize) {
            round()
            position = 0
        }
        return Randomizer.reduceBits<Unit>(bits, Randomizer.foldBits<Unit>(random))
    }
}