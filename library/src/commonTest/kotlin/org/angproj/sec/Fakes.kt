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

import org.angproj.sec.hash.HashHelper
import org.angproj.sec.rand.RandomBits
import org.angproj.sec.rand.Security


object Fakes {

    fun safeSecRand() = object : Security() {
        override val sponge = Stubs.stubSucceedSqueezeSponge()
        init { reseed() }
        override fun checkReseedConditions(): Boolean = true
        override fun reseedImpl() { sponge.scramble() }
        override fun checkExportConditions(bitsNeeded: Long): Boolean {
            reseed()
            return true
        }
    }

    fun unsafeSecRand() = object : Security() {
        override val sponge = Stubs.stubFailSqueezeSponge()
        init { reseed() }
        override fun checkReseedConditions(): Boolean = true
        override fun reseedImpl() { sponge.scramble() }
        override fun checkExportConditions(bitsNeeded: Long): Boolean {
            reseed()
            return true
        }
    }

    fun safeRandomBits(): RandomBits {
        val squeezer = HashHelper(Stubs.stubSucceedSqueezeSponge(), 0,HashHelper.HashMode.SQUEEZING).squeezer
        return RandomBits { RandomBits.compactBitEntropy(it, squeezer.squeeze()) }
    }

    fun safeSecurity(): Security = object : Security() {
        override val sponge = Stubs.stubSucceedSqueezeSponge()
        override fun checkReseedConditions(): Boolean = true //lastReseedBits >= 1024
        override fun reseedImpl() {
            sponge.absorb(1L, 1)
            sponge.scramble()
        }
        //override fun checkExportConditions(length: Int): Boolean = length <= (1024 - lastReseedBits)
        override fun checkExportConditions(bitsNeeded: Long): Boolean {
            if(bitsNeeded <= (1024 - lastReseedBits)) reseed()
            return true
        }
    }
}