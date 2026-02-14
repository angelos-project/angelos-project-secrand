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
import org.angproj.sec.rand.Sponge
import org.angproj.sec.stat.AvalancheEffectTester
import org.angproj.sec.stat.BenchmarkSession
import org.angproj.sec.stat.MonteCarloTester
import org.angproj.sec.stat.SpongeBenchmark
import kotlin.math.PI
import kotlin.math.abs


public fun healthCheck(sponge: Sponge): Boolean {
    val objectSponge = SpongeBenchmark(sponge)
    val samplesNeeded = MonteCarloTester.Mode.MODE_64_BIT.size * 10_000_000L / objectSponge.sampleByteSize

    val session = BenchmarkSession(samplesNeeded, objectSponge.sampleByteSize, objectSponge)
    val monteCarlo = session.registerTester { MonteCarloTester(10_000_000, MonteCarloTester.Mode.MODE_64_BIT, it) }
    val avalancheEffect = session.registerTester { AvalancheEffectTester(10_000_000, it) }

    session.startRun()
    repeat(samplesNeeded.toInt()) {
        session.collectSample()
    }
    session.stopRun()
    val results = session.finalizeCollecting()

    println(results[monteCarlo]!!.report)
    println(results[avalancheEffect]!!.report)
    return !(abs(0.5 - results[avalancheEffect]!!.keyValue) > 0.01 ||
            abs(PI - results[monteCarlo]!!.keyValue) > 0.01)
}


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