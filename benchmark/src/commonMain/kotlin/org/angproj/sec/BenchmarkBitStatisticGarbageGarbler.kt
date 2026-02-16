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
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.exp
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


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

        max.forEachIndexed { idx, _ ->
            println("k=${idx + 1}; avg ${avg[idx] / loops}")
            //println("k=${idx + 1}; max $value, avg ${avg[idx] / loops}")
            //println("k=${idx + 1}; max $value, avg ${avg[idx] / loops}, factor ${value / (avg[idx] / loops)}")
        }
        println()
    }
}

public fun calculateBitPatternUniformityAverage() {
    val garbler = SecureRandom
    repeat(32) { exp ->
        val entropy = ByteArray((exp + 1) * 1024) // 16 MiB

        val max = FloatArray(16)
        val avg = FloatArray(16)

        val loops = 10000

        println("average: ${entropy.size * 8 / 4.0 / 16.0}")

        repeat(loops) {
            garbler.readBytes(entropy)
            val bitStat = bitStatisticOf(entropy)

            val average = bitStat.hex.sum() / 16.0
            bitStat.hex.sorted().forEachIndexed { idx, value ->
                //val expectation = 2.0.pow(logExp - idx.toDouble())
                val difference = average - value
                //val difference = abs(expectation - bitStat.runs[idx])
                val deviation = difference.div(average).absoluteValue
                //val tolerance = log(difference +1, idx.toDouble()+1)
                //println("$idx, $average, $value, $difference, $deviation")
                if (deviation > max[idx]) {
                    max[idx] = deviation.toFloat()
                }
                avg[idx] += deviation.toFloat()
            }
        }

        println("Avg runs difference:")

        max.forEachIndexed { idx, _ ->
            println("k=${idx + 1}; avg ${avg[idx] / loops}")
            //println("k=${idx + 1}; max $value, avg ${avg[idx] / loops}")
            //println("k=${idx + 1}; max $value, avg ${avg[idx] / loops}, factor ${value / (avg[idx] / loops)}")
        }
        println()
    }
}


public fun calculateBitBalanceAverage() {
    val garbler = SecureRandom
    repeat(32) { exp ->
        val entropy = ByteArray((exp + 1) * 1024) // 16 MiB

        var max = 0.0
        var avg = 0.0

        val loops = 10000

        println("average: ${entropy.size * 8 / 2.0}")

        repeat(loops) {
            garbler.readBytes(entropy)
            val bitStat = bitStatisticOf(entropy)

            val average = bitStat.total / 2.0
            //val expectation = 2.0.pow(logExp - idx.toDouble())
            val difference = average - bitStat.ones
            //val difference = abs(expectation - bitStat.runs[idx])
            val deviation = difference.div(average).absoluteValue
            //val tolerance = log(difference +1, idx.toDouble()+1)
            //println("$idx, $average, $value, $difference, $deviation")
            max = max(max, deviation)
            avg += deviation
        }

        println("Avg runs difference:")

        println("avg ${avg / loops}")
        //println("max $max, avg ${avg / loops}")
        //println("max $max, avg ${avg / loops}, factor ${max / (avg / loops)}")

        println()
    }
}

public object BenchmarkBitStatistic {
    @JvmStatic
    public fun main(args: Array<String> = arrayOf()) {
        if(args.isNotEmpty()) {
            println("Arguments provided, skipping benchmarks. Arguments: " + args.joinToString(", "))
        }

        //calculateBitBalanceAverage()

        val garbler = SecureRandom
        repeat(32) { exp ->
            val entropy = ByteArray((exp + 1) * 1024) // 16 MiB

            val loops = 10000
            var totalInside = 0

            var bitBalanceFails = 0
            var hexUniformityFails = 0
            var runDistributionFails = 0
            var longRunFails = 0

            println("logExp: ${log2(entropy.size *4 / 4.0)}")

            repeat(loops) {
                garbler.readBytes(entropy)
                //Random.nextBytes(entropy)
                val bitStat = bitStatisticOf(entropy)

                if (!bitStat.checkBitBalance()) bitBalanceFails++
                if (!bitStat.checkHexUniformity()) hexUniformityFails++
                if (!bitStat.checkRunDistribution()) runDistributionFails++
                if (!bitStat.checkLongRuns()) longRunFails++

                if(bitStat.securityHealthCheck()) {
                    totalInside++
                }
            }
            println("Bit balance fails: $bitBalanceFails")
            println("Hex uniformity fails: $hexUniformityFails")
            println("Run distribution fails: $runDistributionFails")
            println("Long run fails: $longRunFails")
            println("Total inside: $totalInside / $loops, percentage: ${totalInside.toDouble() / loops * 100}%")
        }
    }
}

// RMSE 0.008.
public fun stdDev(k: Double, logExp: Double): Double {
    return 0.031 * exp(0.341 * (k - logExp + 8.488))
}


public fun f(k: Double, average: Double): Double {
    return 0.188 * exp(0.299 * abs(k - 8.48)) / sqrt(average)
}

public fun f(average: Double): Double {
    return 0.563 / sqrt(average)
}