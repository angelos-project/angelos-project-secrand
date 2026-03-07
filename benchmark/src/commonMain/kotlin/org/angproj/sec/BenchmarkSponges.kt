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
package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge21024
import org.angproj.sec.rand.AbstractSponge2256
import org.angproj.sec.rand.AbstractSponge2512
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Reseeder
import org.angproj.sec.rand.Sponge
import org.angproj.sec.stat.AvalancheEffectTester
import org.angproj.sec.stat.BenchmarkSuiteBuilder
import org.angproj.sec.stat.ChiSquareTester
import org.angproj.sec.stat.MonteCarloTester
import org.angproj.sec.stat.SpongeBenchmark
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.abs


public fun healthCheck(sponge: Sponge): Boolean {
    Reseeder(sponge).reseed(JitterEntropy)

    val suite = BenchmarkSuiteBuilder.build {
        samples { 10_000_000 }
        article { SpongeBenchmark(sponge) }
        register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, article) }
        register { AvalancheEffectTester(samples, article) }
        register { ChiSquareTester(samples, article) }
    }
    suite.runBlocking()

    println(suite)
    val results = suite.collectResults()
    return !(abs(0.5 - results["AvalancheEffectTester"]!!.keyValue) > 0.01 ||
            abs(PI - results["MonteCarloTester"]!!.keyValue) > 0.01)
}

public object BenchmarkSponges {
    @JvmStatic
    public fun main(args: Array<String> = arrayOf()) {
        if(args.isNotEmpty()) {
            println("Arguments provided, skipping benchmarks. Arguments: " + args.joinToString(", "))
        }

        mapOf(
            "AbstractSponge256" to object : AbstractSponge256() {},
            "AbstractSponge512" to object : AbstractSponge512() {},
            "AbstractSponge1024" to object : AbstractSponge1024() {},
            "AbstractSponge2256" to object : AbstractSponge2256() {},
            "AbstractSponge2512" to object : AbstractSponge2512() {},
            "AbstractSponge21024" to object : AbstractSponge21024() {}
        ).forEach {
            println("Benchmarking: " + it.key)
            println("Result: " + healthCheck(it.value))
            println()
        }
    }
}
