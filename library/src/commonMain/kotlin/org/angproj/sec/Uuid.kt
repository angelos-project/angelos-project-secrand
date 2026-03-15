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
package org.angproj.sec

import org.angproj.sec.util.Octet
import org.angproj.sec.rand.RandomBits

/**
 * Universally Unique Identifier (UUID).
 *
 * This class represents a UUID using two Long values: `upper` and `lower`.
 * It provides functionality to generate random UUIDs, including version 4 UUIDs,
 * and to retrieve the version and variant of the UUID.
 *
 * @property upper The upper 64 bits of the UUID.
 * @property lower The lower 64 bits of the UUID.
 */
public data class Uuid(
    public val upper: Long = 0,
    public val lower: Long = 0
) {
    /**
     * Creates a UUID from a RandomBits instance.
     *
     * @param randomBits the source of randomness.
     */
    public constructor(randomBits: RandomBits) : this(
        RandomBits.nextBitsToLong(randomBits),
        RandomBits.nextBitsToLong(randomBits)
    )

    /**
     * Creates a random UUID using SecureFeed.
     */
    public constructor() : this(SecureFeed)

    /**
     * The version of the UUID.
     */
    public val version: Int
        get() = ((upper ushr 12) and 0xf).toInt()

    /**
     * The variant of the UUID.
     */
    public val variant: Int
        get() = ((lower ushr 62) and 0x3).toInt()

    /**
     * Returns the string representation of the UUID in standard format.
     *
     * @return the UUID as a string.
     */
    public override fun toString(): String = buildString {
        printStr<Unit>(lower, this, printStr<Unit>(upper, this, 0))
    }

    private inline fun<reified E: Any> printStr(src: Long, sb: StringBuilder, counter: Int): Int {
        var cnt = counter
        Octet.writeNet(src, sb, -1, 8) { _, value ->
            if(cnt in hyphens) {
                sb.append('-')
                cnt += 1
            }
            Octet.toHex(value, sb, -1) { _, v ->
                sb.append(v.toInt().toChar())
                cnt += 1
            }
        }
        return cnt
    }

    public companion object Companion {
        private val hyphens = listOf(8,13,18,23)

        /**
         * Generates a random UUID.
         *
         * @return a new random UUID.
         */
        public fun uuid(): Uuid = Uuid()

        private const val uuid4Variant: Long = (-0x80000000L shl 32)
        
        /**
         * Generates a version 4 UUID.
         *
         * @return a new version 4 UUID.
         */
        public fun uuid4(): Uuid = Uuid(
            (RandomBits.nextBitsToLong(SecureFeed) and 0xffffffff_ffff0fffuL.toLong()) or 0x4000,
            (RandomBits.nextBitsToLong(SecureFeed) and 0x3fffffff_ffffffff) or uuid4Variant
        )

        /**
         * The nil UUID (all zeros).
         */
        public val nil: Uuid by lazy { Uuid(0,0) }

        /**
         * The maximum UUID (all ones).
         */
        public val max: Uuid by lazy { Uuid(ULong.MAX_VALUE.toLong(), ULong.MAX_VALUE.toLong()) }
    }
}