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
package org.angproj.sec.rand

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals

class AbstractSpongeTest {
    @Test
    fun testInitializeWrongVisibleSize() {
        assertFailsWith<IllegalArgumentException> {
            object : AbstractSponge(1, 2) {
                override fun round() {  /* Empty */ }
            }
        }
    }

    @Test
    fun testPrivateAccessToPrimes() {
        val sponge = object : AbstractSponge(2, 1) {
            override fun round() { assertEquals(export.size, 16) }
        }
        sponge.round()
    }
}