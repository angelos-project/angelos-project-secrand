/**
 * Copyright (c) 2024-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.sec

import org.angproj.sec.rand.AbstractSecurity
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.RandomBits
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ceilDiv
import org.angproj.sec.util.floorMod

/**
 * SecureFeed is the main secure random feed using a 512-bit sponge.
 * It implements RandomBits and manages reseeding based on usage thresholds.
 */
public object SecureFeed : AbstractSecurity(object : AbstractSponge512() {}), RandomBits {

    private var nextBytes: Long = 0

    init {
        revitalize(true)
    }
    
    internal fun revitalize(needed: Boolean) {
        if (needed) {
            seedEntropy(SecureEntropy)
            nextBytes = AVERAGE_THRESHOLD + hashSqueezer.squeeze().floorMod(AVERAGE_THRESHOLD) - DEVIATION_THRESHOLD
        }
    }

    override fun reseedPolicy(bytesNeeded: Int): Boolean {
        revitalize(bytesExported + bitsExported.ceilDiv(TypeSize.byteBits.toLong()) + bytesNeeded >= nextBytes)
        return true
    }

    /**
     * Generates the next random bits.
     *
     * @param bits the number of bits to generate.
     * @return the generated bits as an integer.
     */
    public override fun nextBits(bits: Int): Int {
        require(bits in 1..TypeSize.intBits) { "Bits must be between 1 and 32" }
        check(reseedPolicy(bits.ceilDiv(TypeSize.byteBits))) { "Export conditions not met" }
        bitsExported += bits
        return RandomBits.compactBitEntropy(bits, hashSqueezer.squeeze())
    }

    /**
     * Average threshold for reseeding the sponge, which is a half gigabyte in longs.
     */
    public const val AVERAGE_THRESHOLD: Long = (1024 * 1024 * 1024) / 2

    /**
     * Deviation threshold for reseeding the sponge, which is a quarter gigabyte in longs.
     */
    public const val DEVIATION_THRESHOLD: Long = AVERAGE_THRESHOLD / 2
}