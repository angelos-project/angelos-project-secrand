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
package org.angproj.sec.rand

import org.angproj.sec.util.TypeSize

/**
 * Qualifies both using ent for a balanced natural random, and with die-harder on a high level when running:
 * > dieharder -g AES_OFB -a -f random.bin
 * with a 32 BG of random generated as a binary blob.
 * */
public abstract class AbstractSponge1024 : AbstractSponge(16, 16) {

    override fun round() {
        val d = sponge[0] xor sponge[5] xor sponge[10] xor sponge[15]
        val r0 = sponge[0] xor sponge[4] xor sponge[8] xor sponge[12]
        val r1 = sponge[1] xor sponge[5] xor sponge[9] xor sponge[13]
        val r2 = sponge[2] xor sponge[6] xor sponge[10] xor sponge[14]
        val r3 = sponge[3] xor sponge[7] xor sponge[11] xor sponge[15]

        val temp = -sponge[15].inv() * 149
        val sponge15 = -sponge[14].inv() * 137
        val sponge14 = -sponge[13].inv() * 113
        val sponge13 = -sponge[12].inv() * 109
        val sponge12 = -sponge[11].inv() * 101
        val sponge11 = -sponge[10].inv() * 97
        val sponge10 = -sponge[9].inv() * 89
        val sponge9 = -sponge[8].inv() * 73
        val sponge8 = -sponge[7].inv() * 61
        val sponge7 = -sponge[6].inv() * 53
        val sponge6 = -sponge[5].inv() * 41
        val sponge5 = -sponge[4].inv() * 37
        val sponge4 = -sponge[3].inv() * 29
        val sponge3 = -sponge[2].inv() * 17
        val sponge2 = -sponge[1].inv() * 13
        val sponge1 = -sponge[0].inv() * 5
        val sponge0 = temp.rotateLeft(TypeSize.intBits)

        mask = (mask and -counter.inv() and sponge11 and sponge13 and sponge14) xor
                ((sponge15 and sponge3 and sponge5 and sponge6) * 2) xor
                ((r2 and sponge7 and r3) * 4) xor
                ((r0 and r1) * 8) xor
                (d * 16)

        sponge[0] = sponge0 xor r0
        sponge[1] = sponge1 xor r0
        sponge[2] = sponge2 xor r0
        sponge[3] = sponge3 xor r0
        sponge[4] = sponge4 xor r1
        sponge[5] = sponge5 xor r1
        sponge[6] = sponge6 xor r1
        sponge[7] = sponge7 xor r1
        sponge[8] = sponge8 xor r2
        sponge[9] = sponge9 xor r2
        sponge[10] = sponge10 xor r2
        sponge[11] = sponge11 xor r2
        sponge[12] = sponge12 xor r3
        sponge[13] = sponge13 xor r3
        sponge[14] = sponge14 xor r3
        sponge[15] = sponge15 xor r3

        counter++
    }
}