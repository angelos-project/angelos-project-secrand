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

import org.angproj.sec.util.WriteOctet
import org.mockito.Mock
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals


class SecurityMockitoTest {

    @Mock
    private val security: Security = mock()

   @Test
   fun testTotalBits() {
       whenever(security.totalBits).thenReturn(1_000_000)
       assertEquals(security.totalBits, 1_000_000)
   }

    @Test
    fun testLastReseedBits() {
        whenever(security.lastReseedBits).thenReturn(500_000)
        assertEquals(security.lastReseedBits, 500_000)
    }

    @Test
    fun testReadLongs() {
        val data = LongArray(5)
        val writeOctet: WriteOctet<LongArray, Long> =  { index, value ->
            data[index] = value
        }
        doNothing().whenever(security).readLongs(data, 0, data.size, writeOctet)
        assertEquals(security.readLongs(data, 0, data.size, writeOctet), Unit)
    }

    @Test
    fun testReadBytes() {
        val data = ByteArray(5)
        val writeOctet: WriteOctet<ByteArray, Byte> =  { index, value ->
            data[index] = value
        }
        doNothing().whenever(security).readBytes(data, 0, data.size, writeOctet)
        assertEquals(security.readBytes(data, 0, data.size, writeOctet), Unit)
    }

    @Test
    fun testSecurityHealthCheck() {
        doNothing().whenever(security).securityHealthCheck()
        assertEquals(security.securityHealthCheck(), Unit)
    }
}