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
package org.angproj.sec.util

import org.angproj.sec.hash.HashHelper
import org.angproj.sec.rand.Sponge

public class Hash64(private val sponge: Sponge) {

    public val bitSize: Int
        get() = sponge.bitSize
    public val byteSize: Int
        get() = sponge.byteSize
    public val visibleSize: Int
        get() = sponge.visibleSize

    private val hashHelper = HashHelper(sponge)

    private var state = RunState.INITIALIZE

    public fun init() {
        check(state == RunState.INITIALIZE)
        state = RunState.RUNNING
    }

    public fun<E> update(src: E, offset: Int, size: Int, readOctet: ReadOctet<E, Long>) {
        check(state == RunState.RUNNING)

        val absorber = hashHelper.absorber
        repeat(size) {
            absorber.absorb(src.readOctet(offset+it))
        }
    }

    public fun<E> final(dst: E, offset: Int, size: Int, writeOctet: WriteOctet<E, Long>) {
        require(size == sponge.visibleSize)
        check(state == RunState.RUNNING)

        state = RunState.FINISHED
        hashHelper.switchMode()

        val squeezer = hashHelper.squeezer
        repeat(sponge.visibleSize) {
            dst.writeOctet(offset+it, squeezer.squeeze())
        }
    }

    public fun reset() {
        sponge.reset()
        state = RunState.INITIALIZE
    }
}