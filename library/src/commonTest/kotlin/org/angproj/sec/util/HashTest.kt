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

import org.angproj.sec.util.Octet.asHexSymbols
import kotlin.test.Test
import kotlin.test.assertEquals


class HashTest {

    @Test
    fun testUpdateAndFinalByteArray() {
        val result = hashDigestOf(hash256()) {
            val num = "a".repeat(100).encodeToByteArray()
            repeat(10_000) {
                update(num)
            }
        }

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            result.asHexSymbols()
        )
    }

    @Test
    fun testUpdateAndFinalGeneric() {
        val hash = hash256()
        hash.init()

        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num)
        }

        val result = hash.final()

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            result.asHexSymbols()
        )
    }

    @Test
    fun testUpdateWithReadOctet() {
        val hash = hash256()
        hash.init()

        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num)
        }

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            hash.final().asHexSymbols()
        )
    }

    @Test
    fun testFinalStateTransition() {
        val hash = hash256()
        hash.init()

        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num)
        }

        val result = hash.final()

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            result.asHexSymbols()
        )
    }
}