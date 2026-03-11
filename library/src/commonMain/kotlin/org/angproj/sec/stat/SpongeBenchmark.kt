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
package org.angproj.sec.stat

import org.angproj.sec.hash.HashHelper
import org.angproj.sec.hash.HashHelper.HashMode
import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize

/**
 * Benchmark object for the Sponge cryptographic primitive.
 *
 * This class extends the BenchmarkObject class and provides functionality
 * to benchmark a Sponge instance. It initializes the Sponge by scrambling it
 * and provides methods to retrieve sample byte size and generate the next sample.
 *
 * @param article The Sponge instance to be benchmarked.
 */
public class SpongeBenchmark(article: Sponge, private val generatorMode: GeneratorMode = GeneratorMode.BIT_GEN): BenchmarkArticle<Sponge>(article) {

    public enum class GeneratorMode {
        BIT_GEN, HASH_GEN
    }

    private var counter = 0L

    private val hashHelper = HashHelper(article)

    init {
        when (generatorMode) {
            GeneratorMode.BIT_GEN -> hashHelper.switchMode()
            GeneratorMode.HASH_GEN -> {}
        }
    }

    override fun byteSizeImpl(): Int = article.byteSize

    private fun nextBitGen(sample: ByteArray) {
        val squeezer = hashHelper.squeezer
        repeat(article.visibleSize) {
            Octet.write(
                squeezer.squeeze(),
                sample,
                it * TypeSize.longSize,
                TypeSize.longSize
            ) { index, value ->
                sample[index] = value
            }
        }
        article.round()
    }

    private fun nextHashGen(sample: ByteArray) {
        hashHelper.reset()
        hashHelper.absorber.absorb(counter++)
        hashHelper.switchMode()
        val squeezer = hashHelper.squeezer
        repeat(article.visibleSize) {
            Octet.write(
                squeezer.squeeze(),
                sample,
                it * TypeSize.longSize,
                TypeSize.longSize
            ) { index, value ->
                sample[index] = value
            }
        }
    }

    override fun nextSample(): ByteArray {
        return allocSampleArray().apply {
            when (generatorMode) {
                GeneratorMode.BIT_GEN -> nextBitGen(this)
                GeneratorMode.HASH_GEN -> nextHashGen(this)
            }
        }
    }
}