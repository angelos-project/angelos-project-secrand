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
package org.angproj.sec.rand

import org.angproj.sec.stat.BenchmarkSession
import org.angproj.sec.stat.MonteCarloTester
import org.angproj.sec.stat.RandomBitsBenchmark
import org.angproj.sec.util.TypeSize
import kotlin.math.PI
import kotlin.math.abs

public fun interface RandomBits {

    /**
     * Returns an `Int` containing the specified number of random bits.
     *
     * @param bits Number of bits to retrieve (up to 32).
     * @return Random bits as an `Int`.
     */
    public fun nextBits(bits: Int): Int

    public companion object {
        public fun nextBitsToLong(randomBits: RandomBits): Long {
            return (randomBits.nextBits(TypeSize.intBits) shl TypeSize.intBits).toLong() or (randomBits.nextBits(
                TypeSize.intBits).toLong() and 0xFFFFFFFFL)
        }

        public fun compactBitEntropy(bits: Int, entropy: Long): Int {
            return ((entropy ushr 32).toInt() xor (entropy and 0xffffffff).toInt()) ushr (TypeSize.intBits - bits)
        }

        /**
         * Performs a health check on the given sponge instance using statistical tests.
         * Returns true if the sponge passes the health check, false otherwise.
         *
         * @param sponge The sponge instance to be tested.
         * @return True if the sponge passes the health check, false otherwise.
         */
        protected fun healthCheck(randomBits: RandomBits): Boolean {
            val objectRand = RandomBitsBenchmark(randomBits)
            val samplesNeeded = MonteCarloTester.Mode.MODE_32_BIT.size * 10_000_000L / objectRand.sampleByteSize

            val session = BenchmarkSession(samplesNeeded, objectRand.sampleByteSize, objectRand)
            val monteCarlo = session.registerTester { MonteCarloTester(10_000_000, MonteCarloTester.Mode.MODE_32_BIT, it) }

            session.startRun()
            repeat(samplesNeeded.toInt()) {
                session.collectSample()
            }
            session.stopRun()
            val results = session.finalizeCollecting()

            println(results[monteCarlo]!!.report)
            if(abs(PI - results[monteCarlo]!!.keyValue) > 0.01) return false
            return true
        }

        /**
         * Performs a security health check on the current sponge instance.
         * Throws an exception if the health check fails.
         */
        public fun securityHealthCheck(randomBits: RandomBits) {
            check(healthCheck(randomBits)) { "Security health check failed!" }
        }
    }
}