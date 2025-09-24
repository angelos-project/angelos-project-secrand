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

import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.Randomizer
import org.angproj.sec.rand.Security
import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.ExportOctetByte
import org.angproj.sec.util.ExportOctetLong
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.floorMod

/**
 * SecureFeed is a singleton object that provides a secure random number generator
 * based on the AbstractSponge512 algorithm. It uses a secure entropy source to
 * generate random numbers and provides methods to read random bytes into various
 * data structures.
 *
 * The SecureFeed object is designed to be used in cryptographic applications where
 * high-quality randomness is required.
 */
public object SecureFeed : Security(), ExportOctetLong, ExportOctetByte, Randomizer {

    override val sponge: Sponge = object : AbstractSponge512() {}

    private var next: Long = 0

    init {
        revitalize()
    }

    override fun checkReseedConditions(): Boolean = true

    override fun reseedImpl() {
        SecureEntropy.exportLongs(sponge, 0, sponge.visibleSize) { index, value ->
            sponge.absorb(value, index)
        }
        sponge.scramble()
    }

    /**
     * Rounds the internal counter and checks if reseeding is necessary.
     * If the counter exceeds the next threshold, it reseeds the sponge
     * and resets the counter.
     *
     * This method should be called whenever bytes are read from the generator
     * to ensure that the reseeding logic is applied correctly.
     *
     * @param count The number of bytes to add to the counter.
     */
    private fun revitalize() {
        if (lastReseedBits >= next) {
            next = AVERAGE_THRESHOLD + sponge.squeeze(0).floorMod(AVERAGE_THRESHOLD) - DEVIATION_THRESHOLD
            reseed()
        }
    }

    override fun checkExportConditions(length: Int): Boolean {
        return true
    }

    override fun getNextBits(bits: Int): Int {
        require(bits in 1..32) { "Bits must be between 1 and 32" }
        revitalize()
        return sponge.getNextBits(bits)
    }

    /**
     * Average threshold for reseeding the sponge, which is a half gigabyte.
     */
    public const val AVERAGE_THRESHOLD: Long = Int.MAX_VALUE / 4 * TypeSize.byteBits.toLong()

    /**
     * Deviation threshold for reseeding the sponge, which is a quarter gigabyte.
     */
    public const val DEVIATION_THRESHOLD: Long = AVERAGE_THRESHOLD / 2
}