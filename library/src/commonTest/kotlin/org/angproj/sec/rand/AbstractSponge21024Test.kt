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
import kotlin.test.assertEquals

class AbstractSponge21024Test {

    private val emptyHash = "79be7f41442401bffde34bf348196841555cf3ee86a4cc145a32394fa1977ebde3a1ced98fcf3eabfb6c3d131a25605c031e641ed60ab603a4fe39472142cb1dc6271640c0414e0d94f0febe79ce2c6da2e3cb6df7bd5bd9579bfa94837256dce560f70c2908f1facb4139920c0c95c0de99138448607ec29441a85dee3a1879"

    class Hash21024 : Hash<AbstractSponge21024>(object : AbstractSponge21024() {})

    @Test
    fun testEmpty() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_EMPTY, Hash21024())
        assertEquals(hash, emptyHash)
        println(hash)
    }
}