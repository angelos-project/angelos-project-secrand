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

import org.angproj.sec.util.securelyRandomize
import kotlin.test.Test

class securelyRandomizeTest {

    @Test
    fun testSecurelyRandomizeByteArray() {
        val array = ByteArray(53)
        array.securelyRandomize()
    }

    @Test
    fun testSecurelyRandomizeShortArray() {
        val array = ShortArray(36)
        array.securelyRandomize()
    }

    @Test
    fun testSecurelyRandomizeIntArray() {
        val array = IntArray(25)
        array.securelyRandomize()
    }

    @Test
    fun testSecurelyRandomizeLongArray() {
        val array = ShortArray(17)
        array.securelyRandomize()
    }
}