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
import org.angproj.sec.stat.SpongeBenchmark
import kotlin.math.PI
import kotlin.math.abs

public abstract class Security {

    protected abstract val sponge: Sponge

    private var position: Int = 0

    private var _totalBits: Long = 0
    public val totalBits: Long
        get() = _totalBits


    protected fun Sponge.getNextBits(bits: Int): Int {
        if(position >= visibleSize) {
            round()
            position = 0
        }

        _totalBits += bits
        return Randomizer.reduceBits<Unit>(bits, Randomizer.foldBits<Unit>(squeeze(position++)))
    }

    protected fun healthCheck(sponge: Sponge): Boolean {
        val objectSponge = SpongeBenchmark(sponge)
        val samplesNeeded = MonteCarloTester.Mode.MODE_64_BIT.size * 10_000_000L / objectSponge.sampleByteSize

        val session = BenchmarkSession(samplesNeeded, objectSponge.sampleByteSize, objectSponge)
        val monteCarlo = session.registerTester { MonteCarloTester(10_000_000, MonteCarloTester.Mode.MODE_64_BIT, it) }
        //val avalancheEffect = benchmarkSession.registerTester { AvalancheEffectTester(samplesNeeded, it) }

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

    public fun securityHealthCheck() {
        check(healthCheck(sponge)) { "Security health check failed!" }
    }
}