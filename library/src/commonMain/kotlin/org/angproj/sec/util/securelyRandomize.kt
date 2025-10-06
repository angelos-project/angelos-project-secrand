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
import kotlin.math.min


/**
 * Fills this [ByteArray] with cryptographically secure random bytes.
 *
 * Uses [SecureFeed] to generate random data and writes it into the array.
 * The entire array is overwritten with new random values.
 */
public fun ByteArray.securelyRandomize() {
    var pos = 0
    SecureFeed.readLongs(this, 0, size.ceilDiv(8)) { _, value ->
        var data = value
        repeat(min(8, size - pos)) {
            this@securelyRandomize[pos++] = (data and 0xFF).toByte()
            data = data ushr TypeSize.byteBits
        }
    }
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