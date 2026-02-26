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
import org.angproj.sec.SecureRandomException
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Security
import org.angproj.sec.stat.BitStatisticCollector
import org.angproj.sec.stat.cryptoHealthCheck
import org.angproj.sec.stat.securityHealthCheck
import kotlin.math.max
import kotlin.math.min


public class SecurelyRandomize : BitStatisticCollector() {
    private fun consumeLong(value: Long): Unit = consume<Unit>(value, TypeSize.longBits)

    private fun<E> innerReseed(array: E, size: Int, exporter: Octet.ExportLongs<E>, writeOctet: WriteOctet<E, Byte>) {
        require(size in 0..(32 * 1024)) { "Array size must be between 0 and 32768 bytes." }
        var pos = 0
        var fails = 0
        val mask = (1L shl (TypeSize.longBits - 1))

        do {
            reset()
            //array.fill(0)

            var lastCrypto = snapshot()
            var satisfied = false
            var counter = 0

            exporter.export(array, 0, max(1024 / TypeSize.longSize, size.ceilDiv(8))) { _, value ->
                if(!satisfied) {
                    val len = min(8, size - pos)
                    Octet.write(value, array, pos, len, writeOctet)
                    pos += len
                    if(pos == size) {
                        lastCrypto = snapshot().also {
                            satisfied = it.diff(lastCrypto).cryptoHealthCheck()
                        }
                        if(!satisfied) {
                            //array.fill(0)
                            pos = 0
                        }
                    }
                }
                if(counter++ == 0) setup(value and mask == 0L)
                consumeLong(value)
            }

            finish()
            if(!snapshot().securityHealthCheck()) {
                fails++
                satisfied = false
            }
        } while (!satisfied && fails <= 2)
        if(fails >= 2) ensure<SecureRandomException> {
            SecureRandomException("Catastrophic failure: 2 consecutive failed secure health check attempts.")
        }
    }

    public fun reseed(array: ByteArray, entropySource: Security) {
        innerReseed(array, array.size, entropySource::readLongs) { idx, v ->
            array[idx] = v
        }
    }

    public fun reseed(array: ByteArray, entropySource: JitterEntropy) {
        innerReseed(array, array.size,  entropySource::readLongs) { idx, v ->
            array[idx] = v
        }
    }
}


/**
 * Fills this [ByteArray] with cryptographically secure random bytes.
 *
 * Uses [SecureFeed] to generate random data and writes it into the array.
 * The entire array is overwritten with new random values.
 */
public fun ByteArray.securelyRandomize() {
    /*var pos = 0
    SecureFeed.readLongs(this, 0, size.ceilDiv(8)) { _, value ->
        //var data = value
        Octet.write(value, this, pos, min(8, size - pos)) { _, v ->
            this@securelyRandomize[pos++] = v
        }
        /*repeat(min(8, size - pos)) {
            this@securelyRandomize[pos++] = (data and 0xFF).toByte()
            data = data ushr TypeSize.byteBits
        }*/
    }*/
        SecurelyRandomize().reseed(this, SecureFeed)
}

/**
 * Fills this [ShortArray] with cryptographically secure random short values.
 *
 * Uses [SecureFeed] to generate random data and writes it into the array.
 * The entire array is overwritten with new random values.
 */
public fun ShortArray.securelyRandomize() {
    var pos = 0
    SecureFeed.readLongs(this, 0, size.ceilDiv(4)) { _, value ->
        var data = value
        repeat(min(8, size - pos)) {
            this@securelyRandomize[pos++] = (data and 0xFFFF).toShort()
            data = data ushr TypeSize.shortBits
        }
    }
}

/**
 * Fills this [IntArray] with cryptographically secure random integer values.
 *
 * Uses [SecureFeed] to generate random data and writes it into the array.
 * The entire array is overwritten with new random values.
 */
public fun IntArray.securelyRandomize() {
    var pos = 0
    SecureFeed.readLongs(this, 0, size.ceilDiv(2)) { _, value ->
        var data = value
        repeat(min(8, size - pos)) {
            this@securelyRandomize[pos++] = (data and 0xFFFFFFFF).toInt()
            data = data ushr TypeSize.intBits
        }
    }
}

/**
 * Fills this [LongArray] with cryptographically secure random long values.
 *
 * Uses [SecureFeed] to generate random data and writes it into the array.
 * The entire array is overwritten with new random values.
 */
public fun LongArray.securelyRandomize() {
    SecureFeed.readLongs(this, 0, size) { index, value ->
        this@securelyRandomize[index] = value
    }
}