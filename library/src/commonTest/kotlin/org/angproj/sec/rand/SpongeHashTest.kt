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

abstract class SpongeHashTest<S: Sponge> {

    protected abstract val emptyHash: String
    protected abstract val singleAHash: String
    protected abstract val abcHash: String
    protected abstract val mdHash: String
    protected abstract val aToZHash: String
    protected abstract val nopqHash: String
    protected abstract val alphaNumHash: String
    protected abstract val eightNumHash: String
    protected abstract val millionAHash: String

    protected abstract fun getHashInstance(): Hash<S>

    protected fun printDebug(output: String) {
        if(debug) println(output)
    }

    @Test
    fun testEmpty() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_EMPTY, getHashInstance())
        printDebug(hash)
        assertEquals(hash, emptyHash)
    }

    @Test
    fun testSingleA() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_A, getHashInstance())
        printDebug(hash)
        assertEquals(hash, singleAHash)
    }

    @Test
    fun testAbc() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_ABC, getHashInstance())
        printDebug(hash)
        assertEquals(hash, abcHash)
    }

    @Test
    fun testMessage() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_MD, getHashInstance())
        printDebug(hash)
        assertEquals(hash, mdHash)
    }

    @Test
    fun testAToZ() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_A_TO_Z, getHashInstance())
        printDebug(hash)
        assertEquals(hash, aToZHash)
    }

    @Test
    fun testNopq() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_NOPQ, getHashInstance())
        printDebug(hash)
        assertEquals(hash, nopqHash)
    }

    @Test
    fun testAlphaNum() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_ALPHA_NUM, getHashInstance())
        printDebug(hash)
        assertEquals(hash, alphaNumHash)
    }

    @Test
    fun testEightNum() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_EIGHT_NUM, getHashInstance())
        printDebug(hash)
        assertEquals(hash, eightNumHash)
    }

    @Test
    fun testMillionA() {
        val hash = TestGen().runTest(TestGen.NamedTest.TEST_MILLION_A, getHashInstance())
        printDebug(hash)
        assertEquals(hash, millionAHash)
    }

    companion object {
        val debug: Boolean = false
    }
}