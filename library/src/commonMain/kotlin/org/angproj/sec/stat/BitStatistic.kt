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
import org.angproj.sec.util.bitStatisticCollection
import org.angproj.sec.util.ensure
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.floor
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
public fun BitStatistic.checkBitBalance(tolerance: Double = 4.9): Boolean {
    val n = total / 2.0
    val deviance = (0.563 / sqrt(n)) * n * tolerance
    return ones.toDouble() in (n - deviance)..(n + deviance)
}

public fun BitStatistic.checkHexUniformity(tolerance: Double = 3.7): Boolean {
    val n = hex.sum() / 16.0
    val order = hex.sorted()
    return order.indices.all { kIdx ->
        val deviance = (0.188 * exp(0.299 * abs((kIdx + 1.0) - 8.48)) / sqrt(n)) * n * tolerance
        order[kIdx].toDouble() in (n - deviance)..(n + deviance)
    }
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

public fun BitStatistic.securityHealthCheck(): Boolean {
    ensure<SecureRandomException>(
        total in (1024 * TypeSize.byteBits)..(32 * 1024 * TypeSize.byteBits)
    ) { SecureRandomException("Chunk size not between 1K and 32K bytes long.") }
    return checkBitBalance() && checkHexUniformity() && checkRunDistribution()
}

public fun BitStatistic.cryptoHealthCheck(): Boolean {
    ensure<SecureRandomException>(
        total in (32 * TypeSize.byteBits)..(1024 * TypeSize.byteBits)
    ) { SecureRandomException("Chunk size not between 32 and 1024 bytes long.") }
    return checkLongRuns()
}
