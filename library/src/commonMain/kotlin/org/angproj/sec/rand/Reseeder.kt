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
import org.angproj.sec.util.ceilDiv
import org.angproj.sec.util.ensure
import kotlin.math.max
import kotlin.math.min

public class Reseeder(sponge: Sponge) : AbstractRandom<Sponge>(sponge, sponge.visibleSize){
    /*private fun consumeLong(value: Long): Unit = consume<Unit>(value, TypeSize.longBits)

    private fun innerReseed(sponge: Sponge, size: Int, exporter: Octet.ExportLongs<Sponge>) {
        var pos = size
        var fails = 0
        val mask = (1L shl (TypeSize.longBits - 1))

        do {
            reset()
            sponge.reset()

            var lastCrypto = snapshot()
            var satisfied = false
            var counter = 0

            exporter.export(sponge, 0, 1024 / TypeSize.longSize) { _, value ->
                if(!satisfied) {
                    sponge.absorb(value, pos++)
                    if(pos == size) {
                        lastCrypto = snapshot().also {
                            satisfied = it.diff(lastCrypto).cryptoHealthCheck()
                        }
                        when(satisfied) {
                            true -> sponge.scramble()
                            false -> {
                                sponge.reset()
                                pos = 0
                            }
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
    }*/

    override fun exportSize(): Int = 1024 / TypeSize.longSize

    override fun posProgress(pos: Int): Int = 1

    override fun invalidateState() { obj.reset() }

    override fun digest(
        value: Long,
        pos: Int,
        len: Int,
        writeOctet: WriteOctet<Sponge, Byte>
    ) {
        obj.absorb(value, pos)
    }

    override fun whenSatisfied() {
        obj.scramble()
    }

    public fun reseed(entropySource: Security) {
        innerFill(entropySource::readLongs) { idx, v -> }
    }

    public fun reseed(entropySource: JitterEntropy) {
        innerFill(entropySource::readLongs) { idx, v -> }
    }
}