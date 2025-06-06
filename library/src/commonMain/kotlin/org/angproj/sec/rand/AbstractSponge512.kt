/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

/**
 * Qualifies both using ent for a balanced natural random, and with die-harder on a high level when running:
 * > dieharder -g AES_OFB -a -f random.bin
 * with a 32 BG of random generated as a binary blob.
 * */
public abstract class AbstractSponge512 : AbstractSponge(9, 8) {

    override fun round() {
        val d = sponge[0] xor sponge[4] xor sponge[8]
        val r0 = sponge[0] xor sponge[3] xor sponge[6]
        val r1 = sponge[1] xor sponge[4] xor sponge[7]
        val r2 = sponge[2] xor sponge[5] xor sponge[8]

        val temp = -sponge[8].inv() * 73
        val sponge8 = -sponge[7].inv() * 61
        val sponge7 = -sponge[6].inv() * 53
        val sponge6 = -sponge[5].inv() * 41
        val sponge5 = -sponge[4].inv() * 37
        val sponge4 = -sponge[3].inv() * 29
        val sponge3 = -sponge[2].inv() * 17
        val sponge2 = -sponge[1].inv() * 13
        val sponge1 = -sponge[0].inv() * 5
        val sponge0 = temp.rotateLeft(32)

        mask = (mask and -counter.inv() and sponge0 and sponge3 and sponge6) xor
                ((sponge1 and sponge4 and sponge7 and sponge8) * 2) xor
                ((sponge2 and r2 and sponge5) * 4) xor
                ((r0 and r1) * 8) xor
                (d * 16)

        sponge[0] = sponge0 xor r0
        sponge[1] = sponge1 xor r0
        sponge[2] = sponge2 xor r0
        sponge[3] = sponge3 xor r1
        sponge[4] = sponge4 xor r1
        sponge[5] = sponge5 xor r1
        sponge[6] = sponge6 xor r2
        sponge[7] = sponge7 xor r2
        sponge[8] = sponge8 xor r2

        counter++
    }
}