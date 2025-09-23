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
package org.angproj.sec

import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertTrue

class GarbageGarblerMockitoTest {

    @Test
    fun testDepletedFlagWithMockito() {
        val garbler = spy(GarbageGarbler())
    }
}