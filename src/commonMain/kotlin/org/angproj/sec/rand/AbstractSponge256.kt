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
public abstract class AbstractSponge256 : AbstractSponge(4, 4) {

    override fun round() {
        val d = sponge[0] xor sponge[3]
        val r0 = sponge[0] xor sponge[2]
        val r1 = sponge[1] xor sponge[3]

        val temp = -sponge[3].inv() * 29
        sponge[3] = -sponge[2].inv() * 17
        sponge[2] = -sponge[1].inv() * 13
        sponge[1] = -sponge[0].inv() * 5
        sponge[0] = temp.rotateLeft(32)

        mask = (sponge[0] and sponge[2] and counter and mask) xor
                ((sponge[1] and sponge[3] and counter.inv()) * 2) xor
                ((r0 and r1) * 4) xor
                (d * 8)

        sponge[0] = sponge[0] xor r0
        sponge[1] = sponge[1] xor r0
        sponge[2] = sponge[2] xor r1
        sponge[3] = sponge[3] xor r1

        counter++
    }

    /*override fun squeeze(data: BinaryWritable) {
        data.writeLong((sponge[0] xor (mask * 3)))
        data.writeLong((sponge[1] xor (mask * 7)))
        data.writeLong((sponge[2] xor (mask * 11)))
        data.writeLong((sponge[3] xor (mask * 19)))
    }*/
}