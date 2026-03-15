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

import org.angproj.sec.SecureRandomException
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ensure
import kotlin.math.*

/**
 * Data class representing a snapshot of bit statistics.
 * It holds immutable values for total bits, ones, zeros, hexadecimal distribution, runs, and long runs.
 * Provides various health check functions to validate the randomness of the data.
 */
public data class BitStatisticSnapshot(
    override val total: Int,
    override val ones: Int,
    override val zeros: Int,
    override val hex: List<Int>,
    override val runs: List<Int>,
    override val longRuns: Int
) : BitStatistic

/**
 * Checks if the bit balance (ratio of ones to zeros) is within acceptable tolerance.
 * Uses a statistical test to ensure the number of ones is close to half the total bits.
 *
 * @param tolerance the tolerance factor for deviance, default 4.9.
 * @return true if the balance is acceptable, false otherwise.
 */
public fun BitStatisticSnapshot.checkBitBalance(tolerance: Double = 4.9): Boolean {
    val n = total / 2.0
    val deviance = (0.563 / sqrt(n)) * n * tolerance
    return ones.toDouble() in (n - deviance)..(n + deviance)
}

/**
 * Checks if the hexadecimal distribution is uniform within acceptable tolerance.
 * Ensures that each hex value (0-15) appears roughly equally often.
 *
 * @param tolerance the tolerance factor for deviance, default 3.7.
 * @return true if the distribution is uniform, false otherwise.
 */
public fun BitStatisticSnapshot.checkHexUniformity(tolerance: Double = 3.7): Boolean {
    val n = hex.sum() / 16.0
    val order = hex.sorted()
    return order.indices.all { kIdx ->
        val deviance = (0.188 * exp(0.299 * abs((kIdx + 1.0) - 8.48)) / sqrt(n)) * n * tolerance
        order[kIdx].toDouble() in (n - deviance)..(n + deviance)
    }
}

/**
 * Checks if the run distribution (lengths of consecutive identical bits) is within acceptable tolerance.
 * Validates that runs of various lengths occur as expected in random data.
 *
 * @param tolerance the tolerance factor for deviance, default 5.0.
 * @return true if the run distribution is acceptable, false otherwise.
 */
public fun BitStatisticSnapshot.checkRunDistribution(tolerance: Double = 5.0): Boolean {
    val logExp = log2(total / 4.0)
    val length = floor(logExp).toInt() - 3 // Check runs up to log2(n/4) - 3
    return runs.indices.all { kIdx ->
        when (kIdx) {
            in 0..length -> {
                val expectation = 2.0.pow(logExp - kIdx.toDouble())
                val deviance = (0.031 * exp(0.341 * ((kIdx + 1.0) - logExp + 8.488))) * expectation * tolerance
                runs[kIdx].toDouble() in (expectation - deviance)..(expectation + deviance)
            }

            else -> true
        }
    }
}

/**
 * Checks if there are no long runs (runs longer than 20 bits).
 * In good random data, long runs should be absent.
 *
 * @return true if no long runs are present, false otherwise.
 */
public fun BitStatisticSnapshot.checkLongRuns(): Boolean = longRuns == 0

/**
 * Performs a Chi-Square test on the hexadecimal distribution.
 * Measures how well the observed hex frequencies match the expected uniform distribution.
 *
 * @return true if the Chi-Square value is below the threshold, false otherwise.
 */
public fun BitStatisticSnapshot.checkChiSquare(): Boolean {
    val sum = hex.sum()
    val expectedAverage = sum / hex.size.toDouble()
    var chiSquare = 0.0
    hex.forEach {
        val difference: Double = it - expectedAverage
        chiSquare += (difference * difference) / expectedAverage
    }
    return chiSquare < 24.996
}

/**
 * Performs a comprehensive security health check on the bit statistics.
 * Includes checks for bit balance, hex uniformity, run distribution, and Chi-Square.
 * Requires the total bits to be between 1K and 32K bits.
 *
 * @return true if all checks pass, false otherwise.
 */
public fun BitStatisticSnapshot.securityHealthCheck(): Boolean {
    ensure<SecureRandomException>(
        total in (1024 * TypeSize.byteBits)..(32 * 1024 * TypeSize.byteBits)
    ) { SecureRandomException("Chunk size not between 1K and 32K bytes long.") }
    return checkBitBalance() && checkHexUniformity() && checkRunDistribution() && checkChiSquare()
}

/**
 * Performs a cryptographic health check on the bit statistics.
 * Includes checks for long runs and Chi-Square.
 * Allows total bits from 0 to 32K bits.
 *
 * @return true if all checks pass, false otherwise.
 */
public fun BitStatisticSnapshot.cryptoHealthCheck(): Boolean {
    ensure<SecureRandomException>(
        total in 0..(32 * 1024 * TypeSize.byteBits)
    ) { SecureRandomException("Chunk size not between 0 and 32K bytes long.") }
    return checkLongRuns() && checkChiSquare()
}
