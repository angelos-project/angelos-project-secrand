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

import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class MonteCarloTesterTest {

    val samples = 1_000_000L

   @Test
   fun testWithSponge() {
       val objectSponge = SpongeBenchmark(RandomMock())
       val samplesNeeded = 16 * samples / objectSponge.sampleByteSize

       val session = BenchmarkSession(samplesNeeded, objectSponge.sampleByteSize, objectSponge)
       val monteCarlo = session.registerTester { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, it) }

       session.startRun()
       repeat(samplesNeeded.toInt()) {
           session.collectSample()
       }
       session.stopRun()
       val results = session.finalizeCollecting()

       println(results[monteCarlo]!!.report)
       assertTrue(abs(PI - results[monteCarlo]!!.keyValue) < 0.01)
   }

    @Test
    fun testWithRandomBits() {
        val objectRandom = RandomBitsBenchmark(RandomMock())
        val samplesNeeded = 16 * samples / objectRandom.sampleByteSize

        val session = BenchmarkSession(samplesNeeded, objectRandom.sampleByteSize, objectRandom)
        val monteCarlo = session.registerTester { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, it) }

        session.startRun()
        repeat(samplesNeeded.toInt()) {
            session.collectSample()
        }
        session.stopRun()
        val results = session.finalizeCollecting()

        println(results[monteCarlo]!!.report)
        assertTrue(abs(PI - results[monteCarlo]!!.keyValue) < 0.01)
    }
}