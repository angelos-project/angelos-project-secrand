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
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.InitializationVector
import org.angproj.sec.rand.Sponge
import org.angproj.sec.stat.BenchmarkArticle
import org.angproj.sec.stat.BenchmarkTester
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.WriteOctet
import org.angproj.sec.util.ceilDiv
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.min
import kotlin.random.Random

object Stubs {
    fun stubFailSqueezeSponge(seed: Long = 0): Sponge = object : Sponge {
        override val spongeSize: Int = 4
        override val visibleSize: Int = 4
        override val byteSize: Int = visibleSize * TypeSize.longSize
        override val bitSize: Int = visibleSize * TypeSize.longBits

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
            return (counter++).inv().rotateLeft(31) xor state.rotateRight(47) xor InitializationVector.entries.get(position % visibleSize).iv
        }
    }

    fun stubSucceedSqueezeSponge(seed: Long = 0): Sponge = object : AbstractSponge256() {
        init {
            absorb(seed, 0)
            scramble()
        }
    }

    fun stubOctetProducer(): Octet.Producer = object : Octet.Producer {
        override fun <E> exportLongs(dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Long>) {
            val squeezer = stubSucceedSqueezeSponge().squeezerOf()
            repeat(length) {
                dst.writeOctet(offset + it, squeezer())
            }
        }

        override fun <E> exportBytes(dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Byte>) {
            val squeezer = stubSucceedSqueezeSponge().squeezerOf()
            var pos = 0
            repeat(length.ceilDiv(TypeSize.longSize)) { _ ->
                val bytes = min(TypeSize.longSize, length - pos)
                var entropy = squeezer()
                repeat(bytes) {
                    dst.writeOctet(offset + pos++, entropy.toByte())
                    entropy = entropy ushr TypeSize.byteBits
                }
            }        }

    }

    fun<B> stubBenchmarkTester(
        samplesAsked: Long, article: BenchmarkArticle<B>
    ): BenchmarkTester<B, BenchmarkArticle<B>> = Mocks.mockBenchmarkTester(samplesAsked, 16, article)

    fun stubBenchmarkArticle(): BenchmarkArticle<Random> = Mocks.mockBenchmarkArticle(16)
}