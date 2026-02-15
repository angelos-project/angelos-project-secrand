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

import org.angproj.sec.stat.bitStatisticOf
import org.angproj.sec.stat.*
import kotlin.math.absoluteValue
import kotlin.math.exp
import kotlin.math.log2
import kotlin.math.pow


public fun calculateBitRunDistributionAverage() {
    val garbler = SecureRandom
    repeat(32) { exp ->
        val entropy = ByteArray((exp + 1) * 1024) // 16 MiB

        val max = FloatArray(20)
        val avg = FloatArray(20)

        val loops = 10000

        println("logExp: ${log2(entropy.size *8 / 4.0)}")

        repeat(loops) {
            garbler.readBytes(entropy)
            val bitStat = bitStatisticOf(entropy)

            val logExp = log2(bitStat.total / 4.0)
            (0..logExp.toInt()).forEach { idx ->
                val expectation = 2.0.pow(logExp - idx.toDouble())
                val difference = expectation - bitStat.runs[idx]
                //val difference = abs(expectation - bitStat.runs[idx])
                val deviation = difference.div(expectation).absoluteValue
                //val tolerance = log(difference +1, idx.toDouble()+1)
                //println("$idx, $expectation, $difference, $deviation, $tolerance")
                if (deviation > max[idx]) {
                    max[idx] = deviation.toFloat()
                }
                avg[idx] += deviation.toFloat()
            }
        }

        println("Avg runs difference:")

        max.forEachIndexed { idx, value ->
            println("k=${idx + 1}; avg ${avg[idx] / loops}")
            //println("k=${idx + 1}; max $value, avg ${avg[idx] / loops}")
            //println("k=${idx + 1}; max $value, avg ${avg[idx] / loops}, factor ${value / (avg[idx] / loops)}")
        }
        println()
    }
}


public fun main(args: Array<String> = arrayOf()) {
    if(args.isNotEmpty()) {
        println("Arguments provided, skipping benchmarks. Arguments: " + args.joinToString(", "))
    }

    val garbler = SecureRandom
    val entropy = ByteArray(8 * 1024) // 16 MiB

    val loops = 10000
    var totalInside = 0

    println("logExp: ${log2(entropy.size *4 / 4.0)}")

    repeat(loops) {
        garbler.readBytes(entropy)
        val bitStat = bitStatisticOf(entropy)
        if(bitStat.checkRunDistribution()) {
            totalInside++
        }
    }
    println("Total inside: $totalInside / $loops, percentage: ${totalInside.toDouble() / loops * 100}%")


    /*var fails = 0
    var total = 0
    var bitBalanceFails = 0
    var patternUniformityFails = 0
    var runsFails = 0
    var entropyFails = 0
    var longRunsFails = 0


    while (total < 100) {
        garbler.readBytes(entropy)
        //Random.nextBytes(entropy)
        total++
        val bitStat = bitStatisticOf(entropy)
        if (!bitStat.checkBitBalance()) bitBalanceFails++
        if (!bitStat.checkPatternUniformity()) patternUniformityFails++
        if (!bitStat.checkRuns()) runsFails++
        if (!bitStat.checkEntropy(0.99)) entropyFails++
        if (!bitStat.checkLongRuns()) longRunsFails++
        if (!bitStat.isValid()) fails++
    }
    println("Bit balance fails: $bitBalanceFails")
    println("Pattern uniformity fails: $patternUniformityFails")
    println("Runs fails: $runsFails")
    println("Entropy fails: $entropyFails")
    println("Long runs fails: $longRunsFails")
    println("Fails: $fails, Total: $total, Fail rate: ${fails.toDouble() / total}")

    println(bitStatisticOf(entropy).toReport())*/
}

// RMSE 0.008.
public fun stdDev(k: Double, logExp: Double): Double {
    return 0.031 * exp(0.341 * (k - logExp + 8.488))
}