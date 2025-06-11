package org.angproj.sec

import org.angproj.sec.rand.AbstractRandom
import org.angproj.sec.rand.Entropy

/*public fun main() {
    val samples = 10_000_000
    val benchmark = MonteCarlo(samples) {
        SecureRandom
    }
    benchmark.estimatePi()
    println(benchmark)

    val benchmark2 = MonteCarlo(samples) {
        object : AbstractRandom() {
            init {
                refill()
            }

            override fun refill() {
                SecureEntropy.exportLongs(buffer, 0, buffer.size) { index, value ->
                    buffer[index] = value
                }
            }
        }
    }
    benchmark2.estimatePi()
    println(benchmark2)

    val benchmark3 = MonteCarlo(samples) {
        object : AbstractRandom() {
            init {
                refill()
            }

            override fun refill() {
                Entropy.exportLongs(buffer, 0, buffer.size) { index, value ->
                    buffer[index] = value
                }
            }
        }
    }
    benchmark3.estimatePi()
    println(benchmark3)
}*/