package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.stat.AvalancheEffectTester
import org.angproj.sec.stat.BenchmarkSession
import org.angproj.sec.stat.MonteCarloTester
import org.angproj.sec.stat.SpongeBenchmark

public class Benchmark {

}

public fun main(args: Array<String>) {
    println("Hello, Benchmark!")

    val samplesNeeded = 10_000_000L

    val benchmarkSponge = SpongeBenchmark(object : AbstractSponge256() {})
    val benchmarkSession = BenchmarkSession(samplesNeeded, benchmarkSponge.sampleByteSize, benchmarkSponge)
    benchmarkSession.registerTester { MonteCarloTester(samplesNeeded, MonteCarloTester.Mode.MODE_64_BIT, it) }
    benchmarkSession.registerTester { AvalancheEffectTester(samplesNeeded, it) }
    benchmarkSession.startRun()
    repeat(samplesNeeded.toInt()) {
        benchmarkSession.collectSample()
    }
    benchmarkSession.stopRun()
    val results = benchmarkSession.finalizeCollecting()
    println(results["MonteCarlo"]!!.report)
    println(results["AvalancheEffect"]!!.report)
}