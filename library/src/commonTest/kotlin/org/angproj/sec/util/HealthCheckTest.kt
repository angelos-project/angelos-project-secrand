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

import org.angproj.sec.Fakes
import org.angproj.sec.Sampler
import org.angproj.sec.Stubs
import org.angproj.sec.hash.squeezerOf
import org.angproj.sec.rand.RandomBits
import org.angproj.sec.stat.securityHealthCheck
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class HealthCheckTest {

    @Test
    fun testAnalyzeBitsFail() {
        val squeezer =  Stubs.stubFailSqueezeSponge().squeezerOf()
        val bits = RandomBits { RandomBits.compactBitEntropy(TypeSize.intBits, squeezer()) }

        val result = HealthCheck().analyzeBits(bits).securityHealthCheck()

        assertFalse(result)
    }

    @Test
    fun testAnalyzeBitsSucceed() {
        val squeezer =  Stubs.stubSucceedSqueezeSponge().squeezerOf()
        val bits = RandomBits { RandomBits.compactBitEntropy(TypeSize.intBits, squeezer()) }

        val result = HealthCheck().analyzeBits(bits).securityHealthCheck()

        assertTrue(result)
    }

    @Test
    fun testAnalyzeSpongeFail() {
        val sponge = Stubs.stubFailSqueezeSponge()

        val result = HealthCheck().analyzeSponge(sponge).securityHealthCheck()

        assertFalse(result)
    }

    @Test
    fun testAnalyzeSpongeSucceed() {
        val sponge = Stubs.stubSucceedSqueezeSponge()

        val result = HealthCheck().analyzeSponge(sponge).securityHealthCheck()

        assertTrue(result)
    }

    @Test
    fun testAnalyzeIterFail() {
        val iter = object : Iterator<Long> {
            private var squeezer = Stubs.stubFailSqueezeSponge().squeezerOf()
            override fun next(): Long = squeezer()
            override fun hasNext(): Boolean = true
        }

        val result = HealthCheck().analyzeIter(iter).securityHealthCheck()

        assertFalse(result)
    }

    @Test
    fun testAnalyzeIterSucceed() {
        val iter = object : Iterator<Long> {
            private var squeezer = Stubs.stubSucceedSqueezeSponge().squeezerOf()
            override fun next(): Long = squeezer()
            override fun hasNext(): Boolean = true
        }

        val result = HealthCheck().analyzeIter(iter).securityHealthCheck()

        assertTrue(result)
    }

    @Test
    fun testAnalyzeSecurityFail() {
        val security = Fakes.unsafeSecRand()

        val result = HealthCheck().analyzeSecurity(security).securityHealthCheck()

        assertFalse(result)
    }

    @Test
    fun testAnalyzeSecuritySucceed() {
        val security = Fakes.safeSecRand()

        val result = HealthCheck().analyzeSecurity(security).securityHealthCheck()

        assertTrue(result)
    }

    @Test
    fun testAnalyzeByteArrayFail() {
        val sample = Sampler.failedSample()

        val result = HealthCheck().analyzeByteArray(sample).securityHealthCheck()

        assertFalse(result)
    }

    @Test
    fun testAnalyzeByteArraySucceed() {
        val sample = Sampler.healthySample()

        val result = HealthCheck().analyzeByteArray(sample).securityHealthCheck()

        assertTrue(result)
    }

    @Test
    fun testAnalyzeLongsFail() {
        val exporter = Fakes.unsafeSecRand()

        val result = HealthCheck().analyzeLongs(exporter::exportLongs).securityHealthCheck()

        assertFalse(result)
    }

    @Test
    fun testAnalyzeLongsSucceed() {
        val exporter = Fakes.safeSecRand()

        val result = HealthCheck().analyzeLongs(exporter::exportLongs).securityHealthCheck()

        assertTrue(result)
    }

    @Test
    fun testAnalyzeBytesFail() {
        val exporter = Fakes.unsafeSecRand()

        val result = HealthCheck().analyzeBytes(exporter::exportBytes).securityHealthCheck()

        assertFalse(result)
    }

    @Test
    fun testAnalyzeBytesSucceed() {
        val exporter = Fakes.safeSecRand()

        val result = HealthCheck().analyzeBytes(exporter::exportBytes).securityHealthCheck()

        assertTrue(result)
    }

    @Test
    fun testHealthCheck() {
        val sample = Sampler.healthySample()

        val result = HealthCheck.healthCheck { analyzeByteArray(sample) }

        assertTrue(result)
    }

    @Test
    fun testDoubleHealthCheckWithSample() {
        val exporter = Fakes.safeSecRand()
        val sample = ByteArray(1024)

        val result = HealthCheck.doubleHealthCheckWithSample { _ -> analyzeLongs(exporter::exportLongs, sample) }

        assertTrue(result)
        //println(sample.asHexSymbols())
    }

    @Test
    fun testDoubleHealthCheckDebugPrint() {
        val secrand = Fakes.unsafeSecRand()

        println("One (1) purposeful debug print:")
        val result = HealthCheck.doubleHealthCheckDebug { debug -> analyzeLongs(secrand::exportLongs, debug) }

        assertFalse(result)
    }

    @Test
    fun testDoubleHealthCheckDebugPass() {
        val secrand = Fakes.safeSecRand()

        val result = HealthCheck.doubleHealthCheckDebug { debug -> analyzeLongs(secrand::exportLongs, debug) }

        assertTrue(result)
    }
}