/**
 * Copyright (c) 2025-2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.sec.hash


interface SpongeTester {

    val emptyHash: String
    val singleAHash: String
    val abcHash: String
    val mdHash: String
    val aToZHash: String
    val nopqHash: String
    val alphaNumHash: String
    val eightNumHash: String
    val millionAHash: String

    fun testEmpty()

    fun testSingleA()

    fun testAbc()

    fun testMessage()

    fun testAToZ()

    fun testNopq()

    fun testAlphaNum()

    fun testEightNum()

    fun testMillionA()
}