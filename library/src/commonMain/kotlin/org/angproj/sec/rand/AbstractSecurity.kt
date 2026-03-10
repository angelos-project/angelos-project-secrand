/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.hash.HashAbsorber
import org.angproj.sec.hash.HashHelper
import org.angproj.sec.hash.HashSqueezer
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.WriteOctet
import org.angproj.sec.util.ceilDiv
import kotlin.math.min

public abstract class AbstractSecurity(protected val sponge: Sponge): Octet.Producer {
    protected val hashHelper: HashHelper = HashHelper(sponge)
    protected val hashAbsorber: HashAbsorber = hashHelper.absorber
    protected val hashSqueezer: HashSqueezer = hashHelper.squeezer

    protected var bitsExported: Long = 0
    protected var initialized: Boolean = false
    protected var bitCounter: Long = 0
    protected var bytesExported: Int = 0

    public fun <E> seedEntropy(entropySource: Octet.Producer) {
        hashHelper.reset()
        Reseeder(sponge).reseed(entropySource)
        hashHelper.switchMode()
    }

    override fun <E> exportLongs(
        dst: E,
        offset: Int,
        length: Int,
        writeOctet: WriteOctet<E, Long>
    ) {
        if(length <= 0) return

        repeat(length) { index ->
            dst.writeOctet(offset + index, hashSqueezer.squeeze())
        }
    }

    override fun <E> exportBytes(
        dst: E,
        offset: Int,
        length: Int,
        writeOctet: WriteOctet<E, Byte>
    ) {
        if(length <= 0) return

        //check(checkExportConditions(length.toLong() * TypeSize.byteBits)) { "Export conditions not met" }
        var pos = 0
        repeat(length.ceilDiv(TypeSize.longSize)) {
            val bytes = min(TypeSize.longSize, length - pos)
            var entropy = hashSqueezer.squeeze()
            repeat(bytes) {
                dst.writeOctet(offset + pos++, entropy.toByte())
                entropy = entropy ushr TypeSize.byteBits
            }
        }
    }
}