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

public class HashSqueezer(public val sponge: Sponge, private val hashHelper: HashHelper) {
    public fun squeeze(): Long {
        check(hashHelper.mode == HashHelper.HashMode.SQUEEZING) { "Sponge is not available" }
        return sponge.squeeze(hashHelper.position).also { hashHelper.forward() }
    }
}

public fun Sponge.squeezerOf(position: Int = 0): () -> Long {
    val helper = HashHelper(this, position)
    val squeezer = HashSqueezer(this, helper)
    if(helper.mode == HashHelper.HashMode.ABSORBING) helper.switchMode()
    return { squeezer.squeeze() }
}