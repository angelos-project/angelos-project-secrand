/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
 * AbstractSponge is a class that circumvents unnecessary
 * bugs when implementing a secure random sponge construction.
 * */
public abstract class AbstractSponge(
    spongeSize: Int = 0,
    public val visibleSize: Int = 0,
) : EndianAware {

    protected var counter: Long = 0
    protected var mask: Long = 0
    protected val sponge: LongArray = LongArray(spongeSize) { InitializationVector.entries[it + 1].iv }
    public val byteSize: Int = visibleSize * TypeSize.long

    init {
        require(visibleSize <= spongeSize) {
            "Visible size must be equal or less than the number of sponge variables."
        }
    }

    protected abstract fun round()

    protected fun absorb(value: Long, position: Int) {
        val offset = position.floorMod(visibleSize)
        sponge[offset] = sponge[offset] xor value
    }

    protected fun squeeze(position: Int): Long {
        val offset = position.floorMod(visibleSize)
        return sponge[offset] xor (mask * export[offset])
    }

    protected fun scramble() {
        repeat(sponge.size) { round() }
    }

    protected companion object {
        protected val export: List<Int> = listOf(3, 7, 11, 19, 23, 31, 43, 47, 59, 67, 71, 79, 83, 103, 107, 127)
    }
}