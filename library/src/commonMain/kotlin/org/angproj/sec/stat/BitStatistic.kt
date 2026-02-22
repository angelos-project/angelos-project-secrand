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
import org.angproj.sec.rand.RandomBits
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.bitStatisticCollection
import org.angproj.sec.util.ensure
import kotlin.math.*


/**
 * Represents the statistical properties of a byte array in terms of its bits.
 *
 * @property total The total number of bits in the byte array.
 * @property ones The count of bits that are set to 1.
 * @property zeros The count of bits that are set to 0.
 * @property hex A list containing the count of each hexadecimal digit (0-15) in the byte array.
 * @property runs A list where each index k contains the count of runs of length k+1 (consecutive identical bits).
 * @property longRuns The count of runs that exceed a certain length threshold, indicating potential non-randomness.
 */
public data class BitStatistic(
    val total: Int,
    val ones: Int,
    val zeros: Int,
    val hex: List<Int>,
    val runs: List<Int>,
    val longRuns: Int
)


/**
 * Computes the BitStatistic for a given byte array.
 *
 * This function analyzes the byte array to determine the total number of bits, the count of ones and zeros,
 * the distribution of hexadecimal digits, the distribution of runs of identical bits, and the count of long runs.
 *
 * @param entropy The byte array to analyze.
 * @return A BitStatistic object containing the computed statistics.
 */
public fun bitStatisticOf(entropy: ByteArray): BitStatistic = bitStatisticCollection(entropy, entropy.size) { idx ->
    this[idx]
}

// Bit balance: |ones - n/2| < tolerance * sqrt(n/4)
/**
 * Checks if the number of ones in the bit statistic is balanced around half of the total bits, within a specified tolerance.
 *
 * The expected number of ones in a random distribution should be close to half of the total bits. This function calculates
 * a deviance based on the total number of bits and a tolerance factor, and checks if the count of ones falls
 * within this range.
 *
 * The tolerance is tuned such that a factor of just a handful of random samples would fail the test of 10000 samples.
 *
 * @param tolerance A multiplier for the acceptable deviation from the expected number of ones. Default is 4.9.
 * @return True if the number of ones is within the acceptable range, false otherwise.
 */
public fun BitStatistic.checkBitBalance(tolerance: Double = 4.9): Boolean {
    val n = total / 2.0
    val deviance = (0.563 / sqrt(n)) * n * tolerance
    return ones.toDouble() in (n - deviance)..(n + deviance)
}


/**
 * Checks if the distribution of hexadecimal digits in the bit statistic is uniform, within a specified tolerance.
 *
 * The expected count for each hexadecimal digit (0-15) in a random distribution should be close to total/16.
 * This function calculates
 * a deviance for each hexadecimal digit based on its position and the total number of bits, and checks if the
 * count of each digit falls within this range.
 *
 * The tolerance is tuned such that a factor of just a handful of random samples would fail the test of 10000 samples.
 *
 * @param tolerance A multiplier for the acceptable deviation from the expected count for each hexadecimal digit.
 * Default is 3.7.
 * @return True if the distribution of hexadecimal digits is uniform within the acceptable range, false otherwise.
 */
public fun BitStatistic.checkHexUniformity(tolerance: Double = 3.7): Boolean {
    val n = hex.sum() / 16.0
    val order = hex.sorted()
    return order.indices.all { kIdx ->
        val deviance = (0.188 * exp(0.299 * abs((kIdx + 1.0) - 8.48)) / sqrt(n)) * n * tolerance
        order[kIdx].toDouble() in (n - deviance)..(n + deviance)
    }
}

/**
 * Checks if the distribution of runs of identical bits in the bit statistic follows the expected pattern, within a
 * specified tolerance.
 *
 * The expected count for runs of length k in a random distribution can be calculated based on the total number of
 * bits and the run length. This function calculates
 * a deviance for each run length based on its position and the total number of bits, and checks if the count of runs
 * of each length falls within this range.
 *
 * The tolerance is tuned such that a factor of just a handful of random samples would fail the test of 10000 samples.
 *
 * @param tolerance A multiplier for the acceptable deviation from the expected count for each run length. Default is 5.0.
 * @return True if the distribution of runs is within the acceptable range, false otherwise.
 */
public fun BitStatistic.checkRunDistribution(tolerance: Double = 5.0): Boolean {
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

// Long runs: longRuns == 0
/**
 * Checks if there are any long runs of identical bits in the bit statistic.
 *
 * In a random distribution, long runs of identical bits (exceeding a certain length threshold) should be rare or
 * non-existent. This function checks if the count of long runs is zero.
 *
 * @return True if there are no long runs, false otherwise.
 */
public fun BitStatistic.checkLongRuns(): Boolean = longRuns == 0


/**
 * Performs a security health check on the bit statistic by validating the total number of bits and checking various
 * statistical properties.
 *
 * This function ensures that the total number of bits falls within a specified range (between 1K and 32K bytes),
 * and then checks the bit balance, hexadecimal uniformity, and run distribution of the bit statistic.
 * The checks are designed to identify potential weaknesses in the randomness of the data, which could be indicative
 * of random generator depletion or similar issues.
 * The combined tolerance for the checks are approximately ~99.9% with a slight deviance. Therefore, it is very
 * important to make an extra check in case of failures, as a handful of failures in 10000 samples is expected.
 * If the checks fail, it is recommended to perform additional tests or collect more samples to confirm the results
 * before drawing conclusions about the security of the random generator.
 *
 * @return True if all checks pass, false otherwise.
 * @throws SecureRandomException if the total number of bits is not within the specified range.
 */
public fun BitStatistic.securityHealthCheck(): Boolean {
    ensure<SecureRandomException>(
        total in (1024 * TypeSize.byteBits)..(32 * 1024 * TypeSize.byteBits)
    ) { SecureRandomException("Chunk size not between 1K and 32K bytes long.") }
    return checkBitBalance() && checkHexUniformity() && checkRunDistribution()
}

public fun doubleHealthCheck(randomBits: RandomBits): Boolean {
    val sample = ByteArray(1024)
    fun sampleBits(entropy: ByteArray): ByteArray {
        repeat(sample.size / TypeSize.intSize) { idx ->
            Octet.write(
                randomBits.nextBits(TypeSize.intBits).toLong(),
                entropy,
                idx * TypeSize.intSize,
                TypeSize.intSize
            ) { idx, value ->
                entropy[idx] = value
            }
        }
        return entropy
    }
    return bitStatisticOf(
        sampleBits(sample)).securityHealthCheck() ||
            bitStatisticOf(
                sampleBits(sample)).securityHealthCheck()
}


/**
 * Performs a cryptographic health check on the bit statistic by validating the total number of bits and checking
 * for long runs.
 *
 * This function ensures that the total number of bits falls within a specified range (between 32 and 1024 bytes),
 * and then checks for the presence of long runs in the bit statistic.
 *
 * @return True if all checks pass, false otherwise.
 * @throws SecureRandomException if the total number of bits is not within the specified range.
 */
public fun BitStatistic.cryptoHealthCheck(): Boolean {
    ensure<SecureRandomException>(
        total in (32 * TypeSize.byteBits)..(1024 * TypeSize.byteBits)
    ) { SecureRandomException("Chunk size not between 32 and 1024 bytes long.") }
    return checkLongRuns()
}
