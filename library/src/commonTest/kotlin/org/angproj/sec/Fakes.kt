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

import org.angproj.sec.hash.squeezerOf
import org.angproj.sec.rand.Security
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize

object Fakes {
    fun failedSample() = ByteArray(1024).apply {
        val failer = Stubs.stubFailSqueezeSponge().squeezerOf()
        repeat(128) {
            Octet.write(failer(), this, it * TypeSize.longSize, TypeSize.longSize) { index, value ->
                this.set(index, value)
            }
        }
    }

    fun healthySample() = ByteArray(1024).apply {
        val failer = Stubs.stubSucceedSqueezeSponge().squeezerOf()
        repeat(128) {
            Octet.write(failer(), this, it * TypeSize.longSize, TypeSize.longSize) { index, value ->
                this.set(index, value)
            }
        }
    }

    fun safeSecRand() = object : Security() {
        override val sponge = Stubs.stubSucceedSqueezeSponge()
        init { reseed() }
        override fun checkReseedConditions(): Boolean = true
        override fun reseedImpl() { sponge.scramble() }
        override fun checkExportConditions(length: Int): Boolean {
            reseed()
            return true
        }
    }

    fun unsafeSecRand() = object : Security() {
        override val sponge = Stubs.stubFailSqueezeSponge()
        init { reseed() }
        override fun checkReseedConditions(): Boolean = true
        override fun reseedImpl() { sponge.scramble() }
        override fun checkExportConditions(length: Int): Boolean {
            reseed()
            return true
        }
    }
}