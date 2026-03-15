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
package org.angproj.sec.rand

import org.angproj.sec.SecureRandomException
import org.angproj.sec.stat.BitStatisticCollector
import org.angproj.sec.stat.cryptoHealthCheck
import org.angproj.sec.stat.securityHealthCheck
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.WriteOctet
import org.angproj.sec.util.ensure

/**
 * Abstract base class for random number generators that perform health checks.
 * It extends BitStatisticCollector and provides a framework for filling data with random values
 * while ensuring cryptographic and security health checks are passed.
 *
 * @param E the type of the object being randomized.
 * @param T the type of data written.
 * @param obj the object to randomize.
 * @param size the size of the data to generate.
 */
public abstract class AbstractRandom<E, T>(
    protected val obj: E,
    protected val size: Int
): BitStatisticCollector() {

    private fun consumeLong(value: Long): Unit = consume<Unit>(value, TypeSize.longBits)

    protected abstract fun exportSize(): Int

    protected abstract fun posProgress(pos: Int): Int

    protected abstract fun invalidateState()

    protected abstract fun digest(value: Long, pos: Int, len: Int, writeOctet: WriteOctet<E, T>)

    protected abstract fun whenSatisfied()

    protected fun innerFill(exporter: Octet.ExportLongs<E>, writeOctet: WriteOctet<E, T>) {
        require(size in 0..(32 * 1024)) { "Array size must be between 0 and 32768 bytes." }
        if(size == 0) return

        var pos = 0
        var fails = 0

        do {
            invalidateState()

            var lastCrypto = snapshot()
            var satisfied = false
            var counter = 0

            exporter.export(obj, 0, exportSize()) { _, value ->
                if(!satisfied) {
                    val len = posProgress(pos)
                    digest(value, pos, len, writeOctet)
                    pos += len
                    if(pos == size) {
                        lastCrypto = snapshot().also {
                            satisfied = it.diff(lastCrypto).cryptoHealthCheck()
                        }
                        when(satisfied) {
                            true -> whenSatisfied()
                            false -> {
                                invalidateState()
                                pos = 0
                            }
                        }
                    }
                }
                if(counter++ == 0) setup(boolFromIndex<Unit>(0, boolMask<Unit>(TypeSize.longBits), value))
                consumeLong(value)
            }

            finish()
            if(!snapshot().securityHealthCheck()) {
                fails++
                satisfied = false
            }
            reset()
        } while (!satisfied && fails <= 2)
        if(fails >= 2) ensure<SecureRandomException> {
            SecureRandomException("Catastrophic failure: 2 consecutive failed secure health check attempts.")
        }
    }
}