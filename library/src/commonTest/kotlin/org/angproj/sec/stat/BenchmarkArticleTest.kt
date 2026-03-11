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
package org.angproj.sec.stat

import org.angproj.sec.Mocks

import kotlin.test.Test
import kotlin.test.assertEquals

class BenchmarkArticleTest {

    @Test
    fun testSampleByteSize() {
        val article = Mocks.mockBenchmarkArticle(8)

        assertEquals(16, article.sampleByteSize)
    }

    @Test
    fun testAllocSampleArray() {
        val article = Mocks.mockBenchmarkArticle(32)

        val sample = article.nextSample()

        assertEquals(32, sample.size)
    }
}