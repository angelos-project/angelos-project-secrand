/**
 * Copyright (c) 2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.sec.stat

import org.angproj.sec.util.bitStatisticCollection
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.log2


public data class BitStatistic(
    val total: Int,
    val ones: Int,
    val zeros: Int,
    val hex: List<Int>,
    val runs: List<Int>,
    val longRuns: Int
)

public fun bitStatisticOf(entropy: ByteArray): BitStatistic = bitStatisticCollection(entropy, entropy.size) { idx ->
    this[idx]
}

// Bit balance: |ones - n/2| < tolerance * sqrt(n/4)
public fun BitStatistic.checkBitBalance(tolerance: Double = 3.0): Boolean {
    val n = total.toDouble()
    val diff = abs(ones.toDouble() - n / 2)
    return diff < tolerance * sqrt(n / 4)
}

public fun BitStatistic.checkHexUniformity(tolerance: Double = 3.7): Boolean {
    val n = hex.sum() / 16.0
    val order = hex.sorted()
    return order.indices.all { kIdx ->
        val factor = 0.188 * exp(0.299 * abs((kIdx + 1.0) - 8.48)) / sqrt(n)
        val deviance = factor * n * tolerance
        order[kIdx].toDouble() in (n - deviance)..(n + deviance)
    }
}

// Shannon entropy: > threshold * log2(16) for nibbles
public fun BitStatistic.checkEntropy(threshold: Double = 0.99): Boolean {
    val freq = hex.map { it.toDouble() / (total / 4) }  // Nibbles
    val ent = -freq.filter { it > 0 }.sumOf { it * log2(it) }
    return ent > threshold * 4.0  // Max 4 for nibbles
}

public fun BitStatistic.checkRunDistribution(tolerance: Double = 5.0): Boolean {
    val logExp = log2(total / 4.0)
    val length = floor(logExp).toInt()-3 // Check runs up to log2(n/4) - 3
    return runs.indices.all { kIdx ->
        when(kIdx) {
            in 0..length -> {
                val expectation = 2.0.pow(logExp - kIdx.toDouble())
                val deviance = (0.031 * exp(0.341 * ((kIdx + 1.0) - logExp + 8.488))) * expectation * tolerance
                runs[kIdx].toDouble() in (expectation - deviance)..(expectation + deviance)
            }
            else -> true
        }
    }
}

// Long runs: longRuns == 0
public fun BitStatistic.checkLongRuns(): Boolean = longRuns == 0

public fun BitStatistic.isValid(): Boolean = checkBitBalance(3.0)
        && checkHexUniformity()
        && checkEntropy(0.99)
        && checkRunDistribution(5.0)
        && checkLongRuns()

public fun BitStatistic.toReport(): String = """
    Total bits: $total
    Ones: $ones
    Zeros: $zeros
    Hex counts: ${hex.joinToString(", ")}
    Runs counts: ${runs.joinToString(", ")}
    Long runs: $longRuns
""".trimIndent()