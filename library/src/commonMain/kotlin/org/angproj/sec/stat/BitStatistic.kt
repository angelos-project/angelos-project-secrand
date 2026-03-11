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

    public val total: Int
    public val ones: Int
    public val zeros: Int
    public val hex: List<Int>
    public val runs: List<Int>
    public val longRuns: Int

    /**
     * Creates a snapshot of the current BitStatistic, returning a new BitStatisticSnapshot instance that captures
     * the current state of the statistics. This allows for immutability and thread-safety when working with
     * the statistics, as the snapshot will not be affected by any further changes to the original BitStatistic.
     *
     * @return A new BitStatisticSnapshot containing the current values of total, ones, zeros, hex, runs, and longRuns.
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
     * Computes the difference between this BitStatistic and another BitStatistic, returning a new
     * BitStatisticSnapshot that represents the difference. This sample has to be larger or newer
     * than the other which should be older or smaller.
     *
     * @param other The BitStatistic to compare against.
     * @return A new BitStatisticSnapshot representing the difference between this BitStatistic and the other BitStatistic.
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