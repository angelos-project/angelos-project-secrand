package org.angproj.sec.util

import org.angproj.sec.SecureRandom
import kotlin.test.Test
import kotlin.test.assertEquals

class FloorModTest {

    @Test
    fun testFloorMod() {
        repeat(10) {
            val dividend = SecureRandom.readInt()
            val divisor = SecureRandom.readInt()

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
            val dividend = SecureRandom.readLong()
            val divisor = SecureRandom.readLong()

            //println("assertEquals(${dividend.floorMod(divisor)}, ${dividend}.floorMod(${divisor}))")

            assertEquals(
                dividend.floorMod(divisor),
                Math.floorMod(dividend, divisor),
                "Failed for dividend: $dividend, divisor: $divisor"
            )
        }
    }
}