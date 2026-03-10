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

import org.angproj.sec.Fakes
import kotlin.test.Test

class SecurityTest {
    @Test
    fun testFixAndTrix() {
        val sec = Fakes.safeSecurity()
        val data = ByteArray(128)
        sec.exportBytes(data, 0, data.size) {idx, value ->
            this[idx] = value
        }

        println(sec.lastReseedBits)
        println(sec.totalBits)

        sec.exportBytes(data, 0, data.size) {idx, value ->
            this[idx] = value
        }

        println(sec.lastReseedBits)
        println(sec.totalBits)
    }
}