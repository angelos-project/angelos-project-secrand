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
import kotlin.test.Test

class ReseederTest {
    @Test
    fun testReseedSecurity() {
        Reseeder(object : AbstractSponge256() {}).reseed(SecureFeed)
    }

    @Test
    fun testReseedJitterEntropy() {
        Reseeder(object : AbstractSponge256() {}).reseed(JitterEntropy)
    }
}