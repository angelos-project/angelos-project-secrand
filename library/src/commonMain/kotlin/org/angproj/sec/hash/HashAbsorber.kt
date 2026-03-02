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
package org.angproj.sec.hash

import org.angproj.sec.rand.Sponge

public class HashAbsorber(private val sponge: Sponge, private val hashHelper: HashHelper) {
    public fun absorb(value: Long) {
        check(hashHelper.mode == HashHelper.HashMode.ABSORBING) { "Sponge is not available" }
        sponge.absorb(value, hashHelper.position).also { hashHelper.forward() }
    }
}

public fun Sponge.absorberOf(position: Int = 0, slurp: () -> Long): () -> Int {
    val helper = HashHelper(this, position)
    val absorber = HashAbsorber(this, helper)
    if(helper.mode == HashHelper.HashMode.SQUEEZING) helper.switchMode()
    return {
        absorber.absorb(slurp())
        helper.position
    }
}