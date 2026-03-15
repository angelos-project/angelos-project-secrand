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
import org.angproj.sec.rand.AbstractSecurity
import org.angproj.sec.rand.JitterEntropy
import kotlin.math.max
import kotlin.math.min

/**
 * Abstract class for securely randomizing data.
 * It fills the data with random values from an entropy source, performing health checks.
 *
 * @param E the type of the object to randomize.
 * @param obj the object to randomize.
 * @param size the size of the data.
 */
public abstract class SecurelyRandomize<E>(obj: E, size: Int) : AbstractRandom<E, Byte>(obj, size) {

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

    public fun randomize(entropySource: AbstractSecurity, writeOctet: WriteOctet<E, Byte>) {
        innerFill(entropySource::exportLongs, writeOctet)
    }

    public fun randomize(entropySource: JitterEntropy, writeOctet: WriteOctet<E, Byte>) {
        innerFill(entropySource::exportLongs, writeOctet)
    }
}

/**
 * Securely randomizes this byte array with cryptographically secure random data.
 */
public fun ByteArray.securelyRandomize() {
    object : SecurelyRandomize<ByteArray>(this, size) {
        override fun invalidateState() {
            obj.fill(0)
        }
    }.randomize(SecureFeed) { idx, v -> this[idx] = v }
}

/**
 * Securely entropizes this byte array with jitter entropy.
 */
public fun ByteArray.securelyEntropize() {
    object : SecurelyRandomize<ByteArray>(this, size) {
        override fun invalidateState() {
            obj.fill(0)
        }
    }.randomize(JitterEntropy) { idx, v -> this[idx] = v }
}