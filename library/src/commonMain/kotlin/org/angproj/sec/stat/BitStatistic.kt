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

// Nibble chi2: sum((o-e)^2/e) <= critical (df=15, alpha=0.01 ~30.58)
public fun BitStatistic.checkPatternUniformity(alpha: Double = 0.01): Boolean {
    val totalNibbles = total / 4.toDouble()
    val e = totalNibbles / 16
    val chi2 = hex.sumOf { (it.toDouble() - e).pow(2) / e }
    return chi2 <= 30.58  // Approx for alpha=0.01
}

// Runs: for each k, |o - e| < tolerance * sqrt(e)
public fun BitStatistic.checkRuns(tolerance: Double = 3.0): Boolean {
    val n = total.toDouble()
    return runs.indices.all { kIdx ->
        val k = kIdx + 1
        val e = (n - k + 3) / 2.0.pow(k + 1)
        abs(runs[kIdx].toDouble() - e) < tolerance * sqrt(e)
    }
}

// Shannon entropy: > threshold * log2(16) for nibbles
public fun BitStatistic.checkEntropy(threshold: Double = 0.99): Boolean {
    val freq = hex.map { it.toDouble() / (total / 4) }  // Nibbles
    val ent = -freq.filter { it > 0 }.sumOf { it * log2(it) }
    return ent > threshold * 4.0  // Max 4 for nibbles
}

// Long runs: longRuns == 0
public fun BitStatistic.checkLongRuns(): Boolean = longRuns == 0

public fun BitStatistic.isValid(): Boolean = checkBitBalance(3.0)
        && checkPatternUniformity(0.01)
        && checkRuns(3.0)
        && checkEntropy(0.99)
        && checkLongRuns()
