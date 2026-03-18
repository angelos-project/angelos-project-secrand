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

import org.angproj.sec.util.RunState
import kotlin.math.*

/**
 * Abstract base class for collecting bit statistics from random data.
 * It implements the BitStatistic interface and provides protected methods to accumulate
 * statistics such as bit counts, hexadecimal distributions, and run lengths.
 * Subclasses must manage the state and call the appropriate methods to collect data.
 */
public abstract class BitStatisticCollector : BitStatistic {
    protected var _state: RunState = RunState.INITIALIZE

    protected var _total: Int = 0
    protected var _ones: Int = 0
    protected var _zeros: Int = 0
    protected val _hex: IntArray = IntArray(16)
    protected val _runs: IntArray = IntArray(20)
    protected var _longRuns: Int = 0

    override val total: Int get() = _total
    override val ones: Int get() = _ones
    override val zeros: Int get() = _zeros
    override val hex: List<Int> get() = _hex.toList()
    override val runs: List<Int> get() = _runs.toList()
    override val longRuns: Int get() = _longRuns

    protected var run: Int = 0
    protected var last: Boolean = false
    protected var data: Int = 0

    protected fun reset() {
        check(_state == RunState.FINISHED)
        _total = 0
        _ones = 0
        _zeros = 0
        _hex.fill(0)
        _runs.fill(0)
        _longRuns = 0
        run = 0
        last = false
        data = 0
        _state = RunState.INITIALIZE
    }

    protected fun setup(first: Boolean) {
        check(_state == RunState.INITIALIZE) { "Invalid state: $_state, expected INITIALIZE" }
        last = first
        _state = RunState.RUNNING
    }

    protected fun finish() {
        check(_state == RunState.RUNNING) { "Invalid state: $_state, expected RUNNING" }
        if (run > 0) {
            if (run > 20) _longRuns++ else _runs[run - 1]++
        }
        _state = RunState.FINISHED
    }

    protected inline fun<reified E: Any> collectBit(bit: Boolean) {
        _total++
        data = (data shl 1) or if (bit) 1 else 0
        if(_total % 4 == 0) _hex[data and 0xF]++
        if (bit) _ones++ else _zeros++
        if (bit == last) run++ else {
            if (run > 0) {
                if (run > 20) _longRuns++ else _runs[run - 1]++
            }
            last = bit
            run = 1
        }
    }

    protected inline fun<reified E: Any> consume(data: Long, bitSize: Int) {
        check(_state == RunState.RUNNING) { "Invalid state: $_state, expected RUNNING" }
        val boolMask = boolMask<Unit>(bitSize)
        repeat(bitSize) {
            collectBit<Unit>(boolFromIndex<Unit>(it, boolMask, data))
        }
    }
    
    protected inline fun<reified E: Any> boolMask(bitSize: Int): Long = 1L shl (bitSize - 1)
    
    protected inline fun<reified E: Any> boolFromIndex(index: Int, mask: Long, value: Long): Boolean {
        return value and (mask ushr index) != 0L
    }
}