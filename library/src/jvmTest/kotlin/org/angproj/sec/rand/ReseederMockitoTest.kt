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

import org.angproj.sec.SecureFeed
import org.angproj.sec.SecureRandomException
import org.mockito.Mock
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ReseederMockitoTest {

    @Mock
    private val reseeder: Reseeder = mock()

    @Test
    fun testReseedSecurityFail() {
        doThrow(SecureRandomException("")).whenever(reseeder).reseed(SecureFeed)
        assertFailsWith<SecureRandomException> {
            reseeder.reseed(SecureFeed)
        }
    }

    @Test
    fun testReseedJitterEntropyFail() {
        doThrow(SecureRandomException("")).whenever(reseeder).reseed(JitterEntropy)
        assertFailsWith<SecureRandomException> {
            reseeder.reseed(JitterEntropy)
        }
    }
}