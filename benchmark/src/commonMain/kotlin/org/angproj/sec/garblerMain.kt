package org.angproj.sec

import kotlin.time.measureTime

/*public fun main() {
    val garbler = GarbageGarbler()
    val samples = 10_000_000
    val benchmark = MonteCarlo(samples) { garbler }
    benchmark.estimatePi()
    println(benchmark)

    var garbler2 = GarbageGarbler()
    var time = measureTime {
        repeat(Int.MAX_VALUE / (2 * 8)) {
            garbler2.readLong()
        }
    }
    println("1Gb of Long: " + time)

    garbler2 = GarbageGarbler()
    time = measureTime {
        repeat(Int.MAX_VALUE / (2 * 8)) {
            garbler2.readDouble()
        }
    }
    println("1Gb of Double: " + time)

    garbler2 = GarbageGarbler()
    time = measureTime {
        val data = ByteArray(1024)
        repeat(1024 * 1024) {
            garbler2.readBytes(data)
        }
    }
    println("1Gb of ByteArray: " + time)
}*/