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
package org.angproj.sec

import org.angproj.sec.rand.InitializationVector
import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.TypeSize
import kotlin.math.E
import kotlin.math.PI

object Stubs {
    public fun stubFailSqueezeSponge(seed: Long = 0): Sponge = object : Sponge {
        override val spongeSize: Int = 4
        override val visibleSize: Int = 4
        override val byteSize: Int = visibleSize * TypeSize.longSize

        private val ivSize = InitializationVector.entries.size
        private var counter: Long = 1
        private var state: Long = InitializationVector.IV_3569.iv xor seed

        override fun reset() {
            counter = 1
        }

        override fun round() {
            state = state xor (
                    InitializationVector.entries.get((counter * PI).toInt() % ivSize).iv xor
                            InitializationVector.entries.get((counter * E).toInt() % ivSize).iv
            )
            counter++
        }

        override fun absorb(value: Long, position: Int) {
            state = state xor value xor InitializationVector.entries.get(position % visibleSize).iv
        }

        override fun squeeze(position: Int): Long {
            return state xor (state xor InitializationVector.entries.get(position % visibleSize).iv)
        }
    }
}