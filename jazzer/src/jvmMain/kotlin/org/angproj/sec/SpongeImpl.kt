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
package org.angproj.sec

import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.floorMod
import kotlin.math.min


public interface SpongeImpl : Sponge {
    public fun digest(): ByteArray {
        val digestBytes = ByteArray(visibleSize * 8)
        var pos = 0
        repeat(visibleSize) {
            var state = squeeze(it)
            repeat(8) {
                digestBytes[pos++] = (state and 0xff).toByte()
                state = (state ushr 8)
            }
        }
        return digestBytes
    }

    public fun reseed(seed: ByteArray) {
        var position = 0

        // Feed entropy into sponge
        repeat( seed.size / 8 + 1) { idx ->
            val minLoop = min(8, seed.size - position)
            if(minLoop > 0) {
                var entropy = 0L
                repeat(minLoop) {
                    entropy = (entropy shl 8) or seed[position++].toLong()
                }
                absorb(entropy, idx.floorMod(visibleSize))
            }
            round()
        }
    }
}