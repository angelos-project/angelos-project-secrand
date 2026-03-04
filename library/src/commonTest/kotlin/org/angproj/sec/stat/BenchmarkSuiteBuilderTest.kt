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
package org.angproj.sec.stat

import org.angproj.sec.rand.AbstractSponge21024
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Reseeder
import kotlin.test.Test

class BenchmarkSuiteBuilderTest {
    @Test
    fun testCreateTestSuite() {
        val sponge = object : AbstractSponge21024() {}
        Reseeder(sponge).reseed(JitterEntropy)

        val suite = BenchmarkSuiteBuilder.build {
            samples { 10_000_000 }
            article { SpongeBenchmark(sponge) }
            register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, article) }
            register { AvalancheEffectTester(samples, article) }
        }
        suite.runBlocking()

        println(suite)
    }
}