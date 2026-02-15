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

import kotlin.math.sqrt
import kotlin.math.pow

public data class BitStatistic(
    val total: Int,
    val ones: Int,
    val zeros: Int,
    val hex: List<Int>,
    val runs: List<Int>,
    val longRuns: Int
)

// Check if ones and zeros are balanced (within ~3 std devs of n/2)
public fun BitStatistic.checkBitBalance(tolerance: Double = 3.0): Boolean {
    val n = total.toDouble()
    val expected = n / 2
    val stdDev = sqrt(n / 4)
    val lower = expected - tolerance * stdDev
    val upper = expected + tolerance * stdDev
    return ones.toDouble() in lower..upper && zeros.toDouble() in lower..upper
}

// Chi-square test for nibble uniformity (pattern)
public fun BitStatistic.checkPatternUniformity(alpha: Double = 0.01): Boolean {
    val totalNibbles = total / 4
    val expected = totalNibbles.toDouble() / 16
    var chi2 = 0.0
    hex.forEach { observed ->
        chi2 += pow(observed.toDouble() - expected, 2.0) / expected
    }
    // Degrees of freedom = 15, critical value for alpha=0.01 is ~30.58 (from chi-square table)
    val criticalValue = 30.58  // Adjust or use a library for exact quantile
    return chi2 <= criticalValue
}

// Check runs distribution against expected (returns list of booleans for each k=1 to runs.size)
public fun BitStatistic.checkRuns(tolerance: Double = 3.0): List<Boolean> {
    val n = total.toDouble()
    return runs.indices.map { idx ->
        val k = idx + 1
        val expected = (n - k + 3) / pow(2.0, (k + 1).toDouble())
        val stdDev = sqrt(expected)  // Poisson approx
        val lower = expected - tolerance * stdDev
        val upper = expected + tolerance * stdDev
        val observed = runs[idx].toDouble()
        observed in lower.coerceAtLeast(0.0)..upper
    }
}

// Simple check for long runs (should be 0 or very low for crypto randomness)
public fun BitStatistic.checkLongRuns(maxAllowed: Int = 0): Boolean {
    return longRuns <= maxAllowed
}
