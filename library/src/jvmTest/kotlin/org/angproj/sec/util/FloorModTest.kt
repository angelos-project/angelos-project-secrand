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
package org.angproj.sec.util

import org.angproj.sec.SecureRandom
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class FloorModTest {

    @Test
    fun testFloorMod() {
        repeat(10) {
            val dividend = Random.nextInt()
            val divisor = Random.nextInt()

            //println("assertEquals(${dividend.floorMod(divisor)}, ${dividend}.floorMod(${divisor}))")

            assertEquals(
                dividend.floorMod(divisor),
                Math.floorMod(dividend, divisor),
                "Failed for dividend: $dividend, divisor: $divisor"
            )
        }
    }

    @Test
    fun testFloorModLong() {
        repeat(10) {
            val dividend = Random.nextLong()
            val divisor = Random.nextLong()

            //println("assertEquals(${dividend.floorMod(divisor)}, ${dividend}.floorMod(${divisor}))")

            assertEquals(
                dividend.floorMod(divisor),
                Math.floorMod(dividend, divisor),
                "Failed for dividend: $dividend, divisor: $divisor"
            )
        }
    }
}