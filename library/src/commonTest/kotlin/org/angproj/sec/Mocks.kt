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

import org.angproj.sec.rand.AbstractRandom
import org.angproj.sec.rand.Security
import org.angproj.sec.rand.Sponge
import org.angproj.sec.stat.BenchmarkArticle
import org.angproj.sec.stat.BenchmarkTester
import org.angproj.sec.stat.Statistical
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.WriteOctet
import kotlin.math.min
import kotlin.random.Random

object Mocks {
    fun<B> mockBenchmarkTester(samplesAsked: Long, atomicSampleByteSize: Int, article: BenchmarkArticle<B>): BenchmarkTester<B, BenchmarkArticle<B>> = object : BenchmarkTester<B, BenchmarkArticle<B>>(
        samplesAsked, atomicSampleByteSize, article) {
        override fun calculateSampleImpl(sample: ByteArray) {
            totalTakenSamples += min(sample.size.toLong() / atomicSampleByteSize, samplesLeft)
        }
        private fun evaluateSampleData(): Double { return totalTakenSamples.toDouble() }
        override fun collectStatsImpl(): Statistical {
            return Statistical(
                totalTakenSamples,
                evaluateSampleData(),
                duration,
                totalTakenSamples * atomicSampleByteSize,
                toString()
            )
        }

        override fun toString(): String = buildString { append(evaluateSampleData()) }
    }

    fun mockBenchmarkArticle(sampleByteSize: Int): BenchmarkArticle<Random> = object: BenchmarkArticle<Random>(Random) {
        override fun byteSizeImpl(): Int = sampleByteSize
        override fun nextSample(): ByteArray = allocSampleArray().apply { article.nextBytes(this) }
    }

    fun mockSponge(sponge: Sponge): Sponge = object: Sponge {
        var resetMockCount: Int = 0
        var roundMockCount: Int = 0
        var absorbMockCount: Int = 0
        var squeezeMockCount: Int = 0

        private val inner = sponge
        override val spongeSize: Int
            get() = inner.spongeSize
        override val visibleSize: Int
            get() = inner.visibleSize
        override val byteSize: Int
            get() = inner.byteSize
        override val bitSize: Int
            get() = inner.bitSize

        override fun reset() { inner.reset().also { resetMockCount++ } }
        override fun round() { inner.round().also { roundMockCount++ } }
        override fun absorb(value: Long, position: Int) { inner.absorb(value, position).also { absorbMockCount++ } }
        override fun squeeze(position: Int): Long { return inner.squeeze(position).also { squeezeMockCount++ }}
    }

    fun mockAbstractRandom(sponge: Sponge): AbstractRandom<Sponge> = object: AbstractRandom<Sponge>(sponge, sponge.visibleSize){
        var exportSizeMockCount: Int = 0
        var posProgressionMockCount: Int = 0
        var invalidateStateMockCount: Int = 0
        var digestMockCount: Int = 0
        var whenSatisfiedMockCount: Int = 0
        var reseedSecurityMockCount: Int = 0

        override fun exportSize(): Int { return 1024 / TypeSize.longSize.also { exportSizeMockCount++ } }
        override fun posProgress(pos: Int): Int {return 1.also { posProgressionMockCount++ }}
        override fun invalidateState() { obj.reset().also { invalidateStateMockCount++ } }
        override fun digest(value: Long, pos: Int, len: Int, writeOctet: WriteOctet<Sponge, Byte>) {
            obj.absorb(value, pos).also { digestMockCount++ } }
        override fun whenSatisfied() { obj.scramble().also { whenSatisfiedMockCount++ } }
        public fun fillup(entropySource: Security) {
            (innerFill(entropySource::exportLongs) { _, _ -> }).also { reseedSecurityMockCount++ } }
    }
}