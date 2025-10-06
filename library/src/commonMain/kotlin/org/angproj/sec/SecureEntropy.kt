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

import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Security
import org.angproj.sec.rand.Sponge

/**
 * SecureEntropy is a singleton object that provides a secure source of entropy
 * using a sponge construction with a size of 256 bits. It revitalizes the sponge
 * with real-time gated entropy and provides methods to read random bytes.
 */
public object SecureEntropy : Security() {

    override val sponge: Sponge = object : AbstractSponge256() {}

    init {
        reseed()
    }

    override fun checkReseedConditions(): Boolean = true

    override fun reseedImpl() {
        JitterEntropy.readLongs(sponge, 0, sponge.visibleSize) { index, value ->
            sponge.absorb(value, index)
        }
        sponge.scramble()
    }

    override fun checkExportConditions(length: Int): Boolean {
        reseed()
        return true
    }
}