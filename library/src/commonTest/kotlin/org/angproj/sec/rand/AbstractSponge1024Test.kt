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

import org.angproj.sec.rand.AbstractSponge21024Test.Hash21024
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class AbstractSponge1024Test {

    private val emptyHash = "dbd87a113a4a1e7d01567ddec404d2ed914e844e72f078f69288bcdd13ffd3f424c9c7bd33bac9004b5937fccabe8498efd7c8df32d19d6126b6f7a44bcc47db3511f1b7ebccfb705c8d5c092bb4ae8728c3281e201f49615b0b38db8f5a93d0ee6f624ef0af92512061ebf569e95cb44720adedf8cd073bd02ba2d65fa34af3"
    private val singleAHash = "3e45ca38b5daf449b5beb41440a6eb4075cc11f4d5c6d4670db184bfc5ec8870539b336ba7a065b74d2283cb803e084868108bffce8f7cfeaa297fb754cf4def5081066fa947ff5d6938f8cf213774261ff7819e3a51048b9813aaeaca607741245216cc961d65018ad27c7910064440ecd080290c55a647cac40b72e9ff366a"

    class Hash1024 : Hash<AbstractSponge1024>(object : AbstractSponge1024() {})

    @Test
    fun testEmpty() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_EMPTY, Hash1024())
        println(hash)
        assertEquals(hash, emptyHash)
    }

    @Test
    fun testSingleA() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_A, Hash1024())
        println(hash)
        assertEquals(hash, singleAHash)
    }
}