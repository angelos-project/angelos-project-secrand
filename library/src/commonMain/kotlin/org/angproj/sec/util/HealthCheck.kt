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

import org.angproj.sec.rand.RandomBits
import org.angproj.sec.stat.BitStatisticCollector
import org.angproj.sec.stat.BitStatisticSnapshot
import org.angproj.sec.stat.securityHealthCheck
import org.angproj.sec.util.Octet.asHexSymbols


/**
 * A utility class for performing health checks on random bit sources by collecting and analyzing bit statistics.
 *
 * The `HealthCheck` class extends `BitStatisticCollector` to gather statistics about the bits analyzed, such as
 * the total number of bits, the count of ones and zeros, hexadecimal distribution, run lengths, and long runs.
 * It provides methods to analyze bits from various sources, including iterators, byte arrays, and `RandomBits` instances.
 *
 * The class also includes companion object functions to perform health checks on `RandomBits` sources, allowing for
 * both single and double health checks to evaluate the randomness quality of the source.
 */
public class HealthCheck : BitStatisticCollector() {
    private fun consumeLong(value: Long): Unit = consume<Unit>(value, TypeSize.longBits)

    private fun consumeIterator(iter: Iterator<Boolean>) {
        val first = iter.next()
        setup(first)
        collectBit<Unit>(first)
        while (iter.hasNext()) { collectBit<Unit>(iter.next()) }
        finish()
    }

    /**
     * Analyze bits from an iterator and return a snapshot of the collected statistics.
     *
     * @param iter An iterator that provides boolean values representing bits.
     * @return A snapshot of the collected bit statistics after analyzing the provided bits.
     */
    public fun analyze(iter: Iterator<Boolean>): BitStatisticSnapshot {
        consumeIterator(iter)
        return snapshot().also { reset() }
    }

    /**
     * Analyze bits from a source using a provided function to read octets and return a snapshot of the collected statistics.
     *
     * @param src The source from which to read bits.
     * @param size The number of bytes to read from the source.
     * @param readOctet A function that takes an index and returns a byte from the source.
     * @return A snapshot of the collected bit statistics after analyzing the provided bits.
     */
    public fun<E> analyze(
        src: E, size: Int, readOctet: ReadOctet<E, Byte>
    ): BitStatisticSnapshot = analyze(Octet.bitIterator(0..<size, src, readOctet))

    /**
     * Analyze bits from a ByteArray and return a snapshot of the collected statistics.
     *
     * @param src The ByteArray containing the bits to analyze.
     * @return A snapshot of the collected bit statistics after analyzing the provided bits.
     */
    public fun analyze(src: ByteArray): BitStatisticSnapshot = analyze(src, src.size) { idx -> src[idx] }

    /**
     * Analyze bits from a RandomBits source and return a snapshot of the collected statistics.
     *
     * @param randomBits The RandomBits source to analyze.
     * @return A snapshot of the collected bit statistics after analyzing the provided bits.
     */
    public fun analyze(randomBits: RandomBits, debug: Boolean = false): BitStatisticSnapshot {
        val sample = ByteArray(if(debug) 1024 else 0)
        var random = RandomBits.nextBitsToLong(randomBits)
        setup(boolFromIndex<Unit>(0, boolMask<Unit>(TypeSize.longBits), random))
        consumeLong(random)
        if(debug) {
            Octet.write(random, sample, 0, TypeSize.longSize) { idx, value ->
                sample[idx] = value
            }
        }

        repeat((1024 / TypeSize.longSize) - 1) {
            random = RandomBits.nextBitsToLong(randomBits)
            consumeLong(random)
            if(debug) {
                Octet.write(
                    random,
                    sample, (it + 1) * TypeSize.longSize,
                    TypeSize.longSize)
                { idx, value ->
                    sample[idx] = value
                }
            }
        }
        finish()
        if(debug) {
            println("Sample: " + sample.asHexSymbols())
        }
        return snapshot().also { reset() }
    }

    public companion object {

        /**
         * Perform a health check on the provided RandomBits source by analyzing its bits and evaluating
         * the security health check.
         *
         * @param randomBits The RandomBits source to perform the health check on.
         * @return `true` if the security health check passes, `false` otherwise.
         */
        public fun healthCheck(
            randomBits: RandomBits, debug: Boolean = false
        ): Boolean = HealthCheck().analyze(randomBits, debug).securityHealthCheck()

        /**
         * Perform a double health check on the provided RandomBits source by conducting two separate health
         * checks and combining their results.
         *
         * @param randomBits The RandomBits source to perform the double health check on.
         * @return `true` if at least one of the two health checks passes, `false` if both checks fail.
         */
        public fun doubleHealthCheck(
            randomBits: RandomBits, debug: Boolean = false
        ): Boolean {
            val hc1 = healthCheck(randomBits, debug)
            val hc2 = healthCheck(randomBits, debug)
            return hc1 || hc2
        }
    }
}