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
public abstract class AbstractSponge1024 : AbstractSponge(16, 16) {

    override fun round() {
        val d = sponge[0] xor sponge[5] xor sponge[10] xor sponge[15]
        val r0 = sponge[0] xor sponge[4] xor sponge[8] xor sponge[12]
        val r1 = sponge[1] xor sponge[5] xor sponge[9] xor sponge[13]
        val r2 = sponge[2] xor sponge[6] xor sponge[10] xor sponge[14]
        val r3 = sponge[3] xor sponge[7] xor sponge[11] xor sponge[15]

        val temp = -sponge[15].inv() * 149
        sponge[15] = -sponge[14].inv() * 137
        sponge[14] = -sponge[13].inv() * 113
        sponge[13] = -sponge[12].inv() * 109
        sponge[12] = -sponge[11].inv() * 101
        sponge[11] = -sponge[10].inv() * 97
        sponge[10] = -sponge[9].inv() * 89
        sponge[9] = -sponge[8].inv() * 73
        sponge[8] = -sponge[7].inv() * 61
        sponge[7] = -sponge[6].inv() * 53
        sponge[6] = -sponge[5].inv() * 41
        sponge[5] = -sponge[4].inv() * 37
        sponge[4] = -sponge[3].inv() * 29
        sponge[3] = -sponge[2].inv() * 17
        sponge[2] = -sponge[1].inv() * 13
        sponge[1] = -sponge[0].inv() * 5
        sponge[0] = temp.rotateLeft(32)

        mask = (mask and -counter.inv() and sponge[11] and sponge[13] and sponge[14]) xor
                ((sponge[15] and sponge[3] and sponge[5] and sponge[6]) * 2) xor
                ((r2 and sponge[7] and r3) * 4) xor
                ((r0 and r1) * 8) xor
                (d * 16)

        sponge[0] = sponge[0] xor r0
        sponge[1] = sponge[1] xor r0
        sponge[2] = sponge[2] xor r0
        sponge[3] = sponge[3] xor r0
        sponge[4] = sponge[4] xor r1
        sponge[5] = sponge[5] xor r1
        sponge[6] = sponge[6] xor r1
        sponge[7] = sponge[7] xor r1
        sponge[8] = sponge[8] xor r2
        sponge[9] = sponge[9] xor r2
        sponge[10] = sponge[10] xor r2
        sponge[11] = sponge[11] xor r2
        sponge[12] = sponge[12] xor r3
        sponge[13] = sponge[13] xor r3
        sponge[14] = sponge[14] xor r3
        sponge[15] = sponge[15] xor r3

        counter++
    }

    /*override fun squeeze(data: BinaryWritable) {
        data.writeLong((sponge[0] xor (mask * 3)))
        data.writeLong((sponge[1] xor (mask * 7)))
        data.writeLong((sponge[2] xor (mask * 11)))
        data.writeLong((sponge[3] xor (mask * 19)))
        data.writeLong((sponge[4] xor (mask * 23)))
        data.writeLong((sponge[5] xor (mask * 31)))
        data.writeLong((sponge[6] xor (mask * 43)))
        data.writeLong((sponge[7] xor (mask * 47)))
        data.writeLong((sponge[8] xor (mask * 59)))
        data.writeLong((sponge[9] xor (mask * 67)))
        data.writeLong((sponge[10] xor (mask * 71)))
        data.writeLong((sponge[11] xor (mask * 79)))
        data.writeLong((sponge[12] xor (mask * 83)))
        data.writeLong((sponge[13] xor (mask * 103)))
        data.writeLong((sponge[14] xor (mask * 107)))
        data.writeLong((sponge[15] xor (mask * 127)))
    }*/
}