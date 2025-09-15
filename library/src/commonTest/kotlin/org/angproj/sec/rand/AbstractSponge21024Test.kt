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
    private val singleAHash = "1e6e5a27d195698f4ec5b13fbb2265b54937b8a1c760fc7e2b8addb464ec4ebe8b331f3c4f1f6bda4b3d205a64a57a297abc7e5668b6f59605007f16913a32471cb6aa80520f00760cfdb08d39500b2497b2153911e00895b5d7d8b36f0cb99898e3aeea88a037cd170b9eb8b38dd52c1f329e32c959b2ad8d942bd41873f22f"
    private val abcHash = "74d36bd4ff24edf6b75294ae307f16fb4582d5dd68aa6c389630f96712d47434bc82814ba7c30ab72a71a364d44b7e25542f8e8dd21aba7f71926a8c291ce46b523400ca0f4e9b83626c1c58f2896b4f8d933f33f8c065bfc054939b40bab392fda83f237390ae5d6780879ace4b5ec42bdc4d9fc6a3e163342d212426958b1f"
    private val mdHash = "f4c28343a0cdf9d16167acbf343e16bf253ca600c42c2ea64216258435c62d180f108a16670b4a7d3cd7c69858f40ea89c78daa76aaaa5a3eb6f23f4e0faffc53a3628c15580c9b06f1a84be14542c630a8b8665912af8e2d9626a13af31f3ab4956fd49e6fd7bef05325deffe42c8ae2c9ea4754b60146f0fbbc02ceeee7e69"


    class Hash21024 : Hash<AbstractSponge21024>(object : AbstractSponge21024() {})

    @Test
    fun testEmpty() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_EMPTY, Hash21024())
        println(hash)
        assertEquals(hash, emptyHash)
    }

    @Test
    fun testSingleA() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_A, Hash21024())
        println(hash)
        assertEquals(hash, singleAHash)
    }

    @Test
    fun testAbc() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_ABC, Hash21024())
        println(hash)
        assertEquals(hash, abcHash)
    }

    @Test
    fun testMessage() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_MD, Hash21024())
        println(hash)
        assertEquals(hash, mdHash)
    }
}