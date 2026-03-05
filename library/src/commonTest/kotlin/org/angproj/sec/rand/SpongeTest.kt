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

import org.angproj.sec.Sampler
import org.angproj.sec.util.Hash
import org.angproj.sec.util.Octet.asHexSymbols
import org.angproj.sec.util.hashDigestOf
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class SpongeTest<S: Sponge> {

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
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_EMPTY, getHashInstance())
        printDebug(hash)
        assertEquals(hash, emptyHash)
    }

    @Test
    fun testSingleA() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_A, getHashInstance())
        printDebug(hash)
        assertEquals(hash, singleAHash)
    }

    @Test
    fun testAbc() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_ABC, getHashInstance())
        printDebug(hash)
        assertEquals(hash, abcHash)
    }

    @Test
    fun testMessage() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_MD, getHashInstance())
        printDebug(hash)
        assertEquals(hash, mdHash)
    }

    @Test
    fun testAToZ() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_A_TO_Z, getHashInstance())
        printDebug(hash)
        assertEquals(hash, aToZHash)
    }

    @Test
    fun testNopq() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_NOPQ, getHashInstance())
        printDebug(hash)
        assertEquals(hash, nopqHash)
    }

    @Test
    fun testAlphaNum() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_ALPHA_NUM, getHashInstance())
        printDebug(hash)
        assertEquals(hash, alphaNumHash)
    }

    @Test
    fun testEightNum() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_EIGHT_NUM, getHashInstance())
        printDebug(hash)
        assertEquals(hash, eightNumHash)
    }

    @Test
    fun testMillionA() {
        val hash = HashTestGenerator().runTest(HashTestGenerator.NamedTest.TEST_MILLION_A, getHashInstance())
        printDebug(hash)
        assertEquals(hash, millionAHash)
    }

    companion object Companion {
        val debug: Boolean = false
    }
}

class HashTestGenerator {

    enum class NamedTest {
        TEST_EMPTY,
        TEST_A,
        TEST_ABC,
        TEST_MD,
        TEST_A_TO_Z,
        TEST_NOPQ,
        TEST_ALPHA_NUM,
        TEST_EIGHT_NUM,
        TEST_MILLION_A
    }

    fun<E: Sponge> runTest(name: NamedTest, hash: Hash<E>): String = when(name) {
        NamedTest.TEST_EMPTY -> emptyTest(hash)
        NamedTest.TEST_A -> singleATest(hash)
        NamedTest.TEST_ABC -> abcTest(hash)
        NamedTest.TEST_MD -> mdTest(hash)
        NamedTest.TEST_A_TO_Z -> aToZTest(hash)
        NamedTest.TEST_NOPQ -> nopqTest(hash)
        NamedTest.TEST_ALPHA_NUM -> alphaNumTest(hash)
        NamedTest.TEST_EIGHT_NUM -> eightNumTest(hash)
        NamedTest.TEST_MILLION_A -> millionATest(hash)
    }.asHexSymbols()

    private fun<E: Sponge> emptyTest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.emptySample())
    }

    private fun<E: Sponge> singleATest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.singleASample())
    }

    private fun<E: Sponge> abcTest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.abcSample())
    }

    private fun<E: Sponge> mdTest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.messageDigestSample())
    }

    private fun<E: Sponge> aToZTest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.aToZSample())
    }

    private fun<E: Sponge> nopqTest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.nopqSample())
    }

    private fun<E: Sponge> alphaNumTest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.alphaNumSample())
    }

    private fun<E: Sponge> eightNumTest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        update(Sampler.eightNumSample())
    }

    private fun<E: Sponge> millionATest(hash: Hash<E>): ByteArray = hashDigestOf(hash) {
        this.update(Sampler.millionASample())
    }
}