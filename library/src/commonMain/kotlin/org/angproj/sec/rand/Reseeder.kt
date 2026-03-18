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
package org.angproj.sec.rand

import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.WriteOctet

/**
 * Reseeder for a sponge-based random number generator.
 * It reseeds the sponge with entropy from an external source, performing health checks.
 *
 * @param sponge the sponge to reseed.
 */
public class Reseeder(sponge: Sponge) : AbstractRandom<Sponge, Long>(sponge, sponge.visibleSize){

    override fun exportSize(): Int = 1024 / TypeSize.longSize

    override fun posProgress(pos: Int): Int = 1

    override fun invalidateState() { obj.reset() }

    override fun digest(
        value: Long,
        pos: Int,
        len: Int,
        writeOctet: WriteOctet<Sponge, Long>
    ) {
        obj.writeOctet(pos, value)
    }

    override fun whenSatisfied() {
        obj.scramble()
    }

    /**
     * Reseeds the sponge with entropy from the given source.
     *
     * @param entropySource the source of entropy.
     */
    public fun reseed(entropySource: Octet.Producer) {
        innerFill(entropySource::exportLongs) { index, value -> this.absorb(value, index)}
    }
}