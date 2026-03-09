/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.sec.util

import org.angproj.sec.SecureFeed
import org.angproj.sec.rand.AbstractRandom
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Security
import kotlin.math.max
import kotlin.math.min


public abstract class SecurelyRandomize<E>(obj: E, size: Int) : AbstractRandom<E>(obj, size) {

    override fun exportSize(): Int = max(1024 / TypeSize.longSize, size.ceilDiv(8))

    override fun posProgress(pos: Int): Int = min(8, size - pos)

    override fun digest(
        value: Long,
        pos: Int,
        len: Int,
        writeOctet: WriteOctet<E, Byte>
    ) {
        Octet.write(value, obj, pos, len, writeOctet)
    }

    override fun whenSatisfied() {
    /* No additional action needed when satisfied, as the array is already filled with random data. */
    }

    public fun randomize(entropySource: Security, writeOctet: WriteOctet<E, Byte>) {
        innerFill(entropySource::exportLongs, writeOctet)
    }

    public fun randomize(entropySource: JitterEntropy, writeOctet: WriteOctet<E, Byte>) {
        innerFill(entropySource::exportLongs, writeOctet)
    }
}


/**
 * Fills this [ByteArray] with cryptographically secure random bytes.
 *
 * Uses [SecureFeed] to generate random data and writes it into the array.
 * The entire array is overwritten with new random values.
 */
public fun ByteArray.securelyRandomize() {
    object : SecurelyRandomize<ByteArray>(this, size) {
        override fun invalidateState() {
            obj.fill(0)
        }
    }.randomize(SecureFeed) { idx, v -> this[idx] = v }
}

/**
 * Fills this [ByteArray] with cryptographically secure random bytes using jitter entropy.
 *
 * Uses [JitterEntropy] to generate random data and writes it into the array.
 * The entire array is overwritten with new random values.
 */
public fun ByteArray.securelyEntropize() {
    object : SecurelyRandomize<ByteArray>(this, size) {
        override fun invalidateState() {
            obj.fill(0)
        }
    }.randomize(JitterEntropy) { idx, v -> this[idx] = v }
}