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

import kotlin.math.*


public class BitStatisticCollector : BitStatistic {
    private var _total: Int = 0
    private var _ones: Int = 0
    private var _zeros: Int = 0
    private val _hex: IntArray = IntArray(16)
    private val _runs: IntArray = IntArray(20)
    private var _longRuns: Int = 0

    override val total: Int get() = _total
    override val ones: Int get() = _ones
    override val zeros: Int get() = _zeros
    override val hex: List<Int> get() = _hex.toList()
    override val runs: List<Int> get() = _runs.toList()
    override val longRuns: Int get() = _longRuns

    private var run = 0
    private var last = false
    private var data = 0

    protected fun collectBit(bit: Boolean) {
        _total++
        data = (data shl 1) or if (bit) 1 else 0
        if(total % 4 == 0) _hex[data and 0xF]++
        if (bit) _ones++ else _zeros++
        if (bit == last) run++ else {
            if (run > 0) {
                if (run > 20) _longRuns++ else _runs[run - 1]++
            }
            last = bit
            run = 1
        }
    }
}