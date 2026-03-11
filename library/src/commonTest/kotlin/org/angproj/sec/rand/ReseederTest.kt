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

import org.angproj.sec.Fakes
import org.angproj.sec.SecureRandomException
import org.angproj.sec.Stubs
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class ReseederTest {

    @Test
    fun testFailedReseeder() {
        assertFailsWith<SecureRandomException> {
            val reseeder = Reseeder(Stubs.stubSucceedSqueezeSponge())
            reseeder.reseed(Fakes.unsafeSecRand())
        }
    }

    @Test
    fun testSucceedingReseeder() {
        try {
            val reseeder = Reseeder(Stubs.stubSucceedSqueezeSponge())
            reseeder.reseed(Fakes.safeSecRand())
        } catch (e: SecureRandomException) {
            assertFalse(true)
        }
    }

    @Test
    fun testReseedSecurity256() {
        Reseeder(object : AbstractSponge256() {}).reseed(Fakes.safeSecRand())
    }

    @Test
    fun testReseedSecurity512() {
        Reseeder(object : AbstractSponge512() {}).reseed(Fakes.safeSecRand())
    }

    @Test
    fun testReseedSecurity1024() {
        Reseeder(object : AbstractSponge1024() {}).reseed(Fakes.safeSecRand())
    }

    @Test
    fun testReseedSecurity2256() {
        Reseeder(object : AbstractSponge2256() {}).reseed(Fakes.safeSecRand())
    }

    @Test
    fun testReseedSecurity2512() {
        Reseeder(object : AbstractSponge2512() {}).reseed(Fakes.safeSecRand())
    }

    @Test
    fun testReseedSecurity21024() {
        Reseeder(object : AbstractSponge21024() {}).reseed(Fakes.safeSecRand())
    }
}