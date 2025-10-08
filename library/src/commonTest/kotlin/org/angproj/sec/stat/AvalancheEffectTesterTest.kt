package org.angproj.sec.stat

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class AvalancheEffectTesterTest {

    val samples = 1_000_000L

   @Test
   fun testWithSponge() {
       val objectSponge = SpongeBenchmark(RandomMock())
       val samplesNeeded = 16 * samples / objectSponge.sampleByteSize

       val session = BenchmarkSession(samplesNeeded, objectSponge.sampleByteSize, objectSponge)
       val avalancheEffect = session.registerTester { AvalancheEffectTester(samples, it) }

       session.startRun()
       repeat(samplesNeeded.toInt()) {
           session.collectSample()
       }
       session.stopRun()
       val results = session.finalizeCollecting()

       println(results[avalancheEffect]!!.report)
       assertTrue(abs(.5 - results[avalancheEffect]!!.keyValue) < 0.01)
   }

    @Test
    fun testWithRandomBits() {
        val objectRandom = RandomBitsBenchmark(RandomMock())
        val samplesNeeded = 16 * samples / objectRandom.sampleByteSize

        val session = BenchmarkSession(samplesNeeded, objectRandom.sampleByteSize, objectRandom)
        val avalancheEffect = session.registerTester { AvalancheEffectTester(samples, it) }

        session.startRun()
        repeat(samplesNeeded.toInt()) {
            session.collectSample()
        }
        session.stopRun()
        val results = session.finalizeCollecting()

        println(results[avalancheEffect]!!.report)
        assertTrue(abs(.5 - results[avalancheEffect]!!.keyValue) < 0.01)
    }
}