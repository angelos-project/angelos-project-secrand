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

import org.angproj.sec.Stubs
import kotlin.random.Random

import kotlin.test.Test
import kotlin.test.assertEquals

class RandomBitsBenchmarkTest {

    @Test
    fun testRandomBits() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 1 }
            article { RandomBitsBenchmark { Random.nextInt() } }
            register { Stubs.stubBenchmarkTester(samples, article) }
        }

        suite.runBlocking()

        // The stub is an object, that's why classname "null"
        assertEquals(1, suite.collectResults()["null"]!!.sampleCount)
    }
}