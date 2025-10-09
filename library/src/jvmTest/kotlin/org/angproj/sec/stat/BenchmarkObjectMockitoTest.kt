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
package org.angproj.sec.stat

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MockObject

class BenchmarkObjectMockitoTest {

    private lateinit var benchmarkObject: BenchmarkObject<MockObject>

    @BeforeTest
    fun setup() {
        benchmarkObject = mock()
    }

    @Test
    fun testSampleByteSize() {
        whenever(benchmarkObject.sampleByteSize).thenReturn(16)
        assertEquals(benchmarkObject.sampleByteSize, 16)
    }

    @Test
    fun testNextSample() {
        val sample = byteArrayOf(0x11, 0x11, 0x11, 0x11,0x11, 0x11, 0x11, 0x11,0x11, 0x11, 0x11, 0x11,0x11, 0x11, 0x11, 0x11)
        whenever(benchmarkObject.nextSample()).thenReturn(sample)
        assertEquals(benchmarkObject.nextSample(), sample)
    }
}