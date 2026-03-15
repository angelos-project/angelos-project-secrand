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

/**
 * Interface for collecting and providing bit statistics from random data.
 * It defines properties for total bits, counts of ones and zeros, hexadecimal distributions,
 * run lengths, and long runs, along with methods to snapshot and compute differences.
 */
public interface BitStatistic {
    // Also implement ChiSquare, sources:
    // https://www.allresearchjournal.com/archives/2024/vol10issue8/PartA/10-7-53-474.pdf
    // https://courses.physics.illinois.edu/phys598aem/fa2014/Software/Critical_Values_of_the_Chi-Squared_Distribution.pdf
    // https://www.cs.bu.edu/fac/snyder/cs237/Handouts%20and%20Web%20Documents/Handouts/Appendix%20B.pdf
    // https://users.sussex.ac.uk/~grahamh/RM1web/ChiSquareTable2005.pdf
    // https://www.medcalc.org/en/manual/chi-square-table.php
    // https://di-mgt.com.au/chisquare-table.html
    //
    // https://www.itl.nist.gov/div898/handbook/eda/section3/eda3674.htm

    /**
     * The total number of bits analyzed.
     */
    public val total: Int

    /**
     * The number of bits set to 1 (ones).
     */
    public val ones: Int

    /**
     * The number of bits set to 0 (zeros).
     */
    public val zeros: Int

    /**
     * The distribution of hexadecimal values (0-15) from the bits.
     */
    public val hex: List<Int>

    /**
     * The lengths of runs (sequences of identical bits) up to 20 bits.
     */
    public val runs: List<Int>

    /**
     * The number of runs longer than 20 bits.
     */
    public val longRuns: Int

    /**
     * Creates a snapshot of the current bit statistics.
     *
     * @return a BitStatisticSnapshot containing the current values.
     */
    public fun snapshot(): BitStatisticSnapshot = BitStatisticSnapshot(
        total = total,
        ones = ones,
        zeros = zeros,
        hex = hex.toList(),
        runs = runs.toList(),
        longRuns = longRuns
    )

    /**
     * Computes the difference between this BitStatistic and another.
     * The current statistic must be equal or larger/newer than the other.
     *
     * @param other the other BitStatistic to compare against.
     * @return a BitStatisticSnapshot representing the differences.
     */
    public fun diff(other: BitStatistic): BitStatisticSnapshot {
        check(total >= other.total) { "The current must be equal or larger/newer than the other."}
        return BitStatisticSnapshot(
            total = total - other.total,
            ones = ones - other.ones,
            zeros = zeros - other.zeros,
            hex = hex.zip(other.hex) { a, b -> a - b },
            runs = runs.zip(other.runs) { a, b -> a - b },
            longRuns = longRuns - other.longRuns
        )
    }
}