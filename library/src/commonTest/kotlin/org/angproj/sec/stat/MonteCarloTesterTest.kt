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