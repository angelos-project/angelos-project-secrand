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

import org.angproj.sec.rand.AbstractSponge256
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.Test

class BenchmarkSuiteBuilderTest {
    @Test
    fun testCreateTestSuite() {
        val sponge = object : AbstractSponge256() {}

        val suite = BenchmarkSuiteBuilder.build {
            samples { 10_000_000 }
            article { SpongeBenchmark(sponge, SpongeBenchmark.GeneratorMode.BIT_GEN) }
            register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, article) }
            register { AvalancheEffectTester(samples, article) }
            register { ChiSquareTester(samples, article) }
        }
        suite.runBlocking()

        println(suite)
        println(1.0 / sqrt(10000000.0))
        println(PI - (1.0 / sqrt(10000000.0)))
        println(PI + (1.0 / sqrt(10000000.0)))

    }

    @Test
    fun fixAndTrix() {
        println(PI - (1.0 / sqrt(10000000.0)))
    }
}