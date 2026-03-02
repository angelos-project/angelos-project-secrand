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
package org.angproj.sec.util

import org.angproj.sec.Stubs
import org.angproj.sec.hash.squeezerOf
import org.angproj.sec.rand.RandomBits
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class HealthCheckTest {

    @Test
    fun testAnalyzeBitsFail() {
        val squeezer =  Stubs.stubFailSqueezeSponge().squeezerOf()
        val bits = RandomBits { RandomBits.compactBitEntropy(TypeSize.intBits, squeezer()) }

        val result = HealthCheck.healthCheck { debug -> analyzeBits(bits, debug) }

        assertFalse(result)
    }

    @Test
    fun testAnalyzeBitsSucceed() {
        val squeezer =  Stubs.stubSucceedSqueezeSponge().squeezerOf()
        val bits = RandomBits { RandomBits.compactBitEntropy(TypeSize.intBits, squeezer()) }

        val result = HealthCheck.healthCheck { debug -> analyzeBits(bits, debug) }

        assertTrue(result)
    }

    @Test
    fun testAnalyzeSpongeFail() {
        val sponge = Stubs.stubFailSqueezeSponge()

        val result = HealthCheck.healthCheck { debug -> analyzeSponge(sponge, debug) }

        assertFalse(result)
    }

    @Test
    fun testAnalyzeSpongeSucceed() {
        val sponge = Stubs.stubSucceedSqueezeSponge()

        val result = HealthCheck.healthCheck { debug -> analyzeSponge(sponge, debug) }

        assertTrue(result)
    }

    @Test
    fun testAnalyzeIterFail() {
        val iter = object : Iterator<Long> {
            private var squeezer = Stubs.stubFailSqueezeSponge().squeezerOf()
            override fun next(): Long = squeezer()
            override fun hasNext(): Boolean = true
        }

        val result = HealthCheck.healthCheck { debug -> analyzeIter(iter, debug) }

        assertFalse(result)
    }

    @Test
    fun testAnalyzeIterSucceed() {
        val iter = object : Iterator<Long> {
            private var squeezer = Stubs.stubSucceedSqueezeSponge().squeezerOf()
            override fun next(): Long = squeezer()
            override fun hasNext(): Boolean = true
        }

        val result = HealthCheck.healthCheck { debug -> analyzeIter(iter, debug) }

        assertTrue(result)
    }

    @Test
    fun testAnalyze() {
        assertTrue {
            HealthCheck().analyze(Random.nextBytes(1024)).total == 8192
        }
    }

    @Test
    fun testDoubleHealthCheck() {
        assertTrue{
            HealthCheck.healthCheckFailedSample { debug ->
                analyzeBits( { Random.nextBits(32) }, debug)
            }
        }
    }
}