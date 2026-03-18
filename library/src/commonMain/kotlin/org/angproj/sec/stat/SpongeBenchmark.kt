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
import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize

/**
 * A benchmark article for Sponge, generating samples using either bit generation or hash generation modes.
 * In bit generation mode, it squeezes values from the sponge; in hash generation mode, it absorbs a counter and squeezes.
 *
 * @property article The Sponge instance used for generating samples.
 * @property generatorMode The mode of generation (BIT_GEN or HASH_GEN).
 */
public class SpongeBenchmark(article: Sponge, private val generatorMode: GeneratorMode = GeneratorMode.BIT_GEN): BenchmarkArticle<Sponge>(article) {

    /**
     * Enumeration for the generator modes of the sponge benchmark.
     */
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

    /**
     * Generates the next sample based on the generator mode.
     * For BIT_GEN, squeezes from the sponge; for HASH_GEN, absorbs a counter and squeezes.
     *
     * @return A byte array containing the next sample.
     */
    override fun nextSample(): ByteArray {
        return allocSampleArray().apply {
            when (generatorMode) {
                GeneratorMode.BIT_GEN -> nextBitGen(this)
                GeneratorMode.HASH_GEN -> nextHashGen(this)
            }
        }
    }
}