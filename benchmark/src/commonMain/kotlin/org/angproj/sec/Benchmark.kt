package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.stat.BenchmarkSession
import org.angproj.sec.stat.MonteCarloTester
import org.angproj.sec.stat.SpongeBenchmark

public class Benchmark {

}

public fun main(args: Array<String>) {
    println("Hello, Benchmark!")

    val samplesNeeded = 100

    val benchmarkSponge = SpongeBenchmark(object : AbstractSponge256() {})
    val benchmarkSession = BenchmarkSession(samplesNeeded, 32, benchmarkSponge)
    benchmarkSession.registerTester { MonteCarloTester(samplesNeeded, MonteCarloTester.Mode.MODE_64_BIT, it) }
    benchmarkSession.startRun()
    repeat(samplesNeeded) {
        benchmarkSession.collectSample()
    }
    benchmarkSession.stopRun()
    val results = benchmarkSession.finalizeCollecting()
    println(results.get("MonteCarlo")!!.report)
}