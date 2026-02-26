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
import org.angproj.sec.util.HashAbsorber
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ensure

public class Reseeder(private val sponge: Sponge) : BitStatisticCollector() {
    private fun consumeLong(value: Long): Unit = consume<Unit>(value, TypeSize.longBits)

    private fun innerReseed(sponge: Sponge, exporter: Octet.ExportLongs<HashAbsorber>) {
        val absorber = HashAbsorber(sponge)
        var fails = 0
        val mask = (1L shl (TypeSize.longBits - 1))

        do {
            reset()
            sponge.reset()

            var lastCrypto = snapshot()
            var satisfied = false
            var counter = 0

            exporter.export(absorber, 0, 1024 / TypeSize.longSize) { _, value ->
                if(!satisfied) {
                    absorber.absorb(value)
                    if(absorber.position == 0) {
                        lastCrypto = snapshot().also {
                            satisfied = it.diff(lastCrypto).cryptoHealthCheck()
                        }
                        when(satisfied) {
                            true -> sponge.scramble()
                            false -> sponge.reset()
                        }
                    }
                }
                if(counter++ == 0) setup(value and mask == 0L)
                consumeLong(value)
            }

            finish()
            if(!snapshot().securityHealthCheck()) {
                fails++
                satisfied = false
            }
        } while (!satisfied && fails <= 2)
        if(fails >= 2) ensure<SecureRandomException> {
            SecureRandomException("Catastrophic failure: 2 consecutive failed secure health check attempts.")
        }
    }

    public fun reseed(entropySource: Security) {
        innerReseed(sponge, entropySource::readLongs)
    }

    public fun reseed(entropySource: JitterEntropy) {
        innerReseed(sponge, entropySource::readLongs)
    }
}