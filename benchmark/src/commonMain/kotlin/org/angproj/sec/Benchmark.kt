package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge21024
import org.angproj.sec.rand.AbstractSponge2256
import org.angproj.sec.rand.AbstractSponge2512
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.stat.AvalancheEffectTester
import org.angproj.sec.stat.BenchmarkSession
import org.angproj.sec.stat.ChiSquareTester
import org.angproj.sec.stat.MonteCarloTester
import org.angproj.sec.stat.SpongeBenchmark

public class Benchmark {

}

public fun main(args: Array<String>) {
    /*var criticalValue = 0.0
    val time = measureTime {
        criticalValue = ChiSquareTester.criticalValue()
    }
    println(criticalValue)
    println(time)

    println("Hello, Benchmark!")
    println("Running security health checks...")

    println("SecureEntropy:")
    SecureEntropy.securityHealthCheck()

    println("SecureFeed:")
    SecureFeed.securityHealthCheck()*/

    listOf(
        object : AbstractSponge256() {},
        object : AbstractSponge512() {},
        object : AbstractSponge1024() {},
        object : AbstractSponge2256() {},
        object : AbstractSponge2512() {},
        object : AbstractSponge21024() {}
    ).forEach {
        println(it.visibleSize)
        val benchmarkSponge = SpongeBenchmark(it)
        val samplesNeeded = MonteCarloTester.Mode.MODE_64_BIT.size * 10_000_000L / benchmarkSponge.sampleByteSize
        val benchmarkSession = BenchmarkSession(samplesNeeded, benchmarkSponge.sampleByteSize, benchmarkSponge)
        val monteCarlo = benchmarkSession.registerTester { MonteCarloTester(samplesNeeded, MonteCarloTester.Mode.MODE_64_BIT, it) }
        val avalancheEffect = benchmarkSession.registerTester { AvalancheEffectTester(samplesNeeded, it) }
        val chiSquare = benchmarkSession.registerTester { ChiSquareTester(samplesNeeded, it) }
        benchmarkSession.startRun()
        repeat(samplesNeeded.toInt()) {
            benchmarkSession.collectSample()
        }
        benchmarkSession.stopRun()
        val results = benchmarkSession.finalizeCollecting()
        println(results[monteCarlo]!!.report)
        println(results[avalancheEffect]!!.report)
        println(results[chiSquare]!!.report)
        println()
    }
}