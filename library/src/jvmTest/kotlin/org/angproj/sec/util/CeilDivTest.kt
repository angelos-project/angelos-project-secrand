package org.angproj.sec.util

import org.angproj.sec.SecureRandom
import kotlin.test.Test
import kotlin.test.assertEquals

class CeilDivTest {

    @Test
    fun testCeilDiv() {
        repeat(10) {
            val dividend = SecureRandom.readInt()
            val divisor = SecureRandom.readShort().toInt()

            //println("assertEquals(${dividend.ceilDiv(divisor)}, ${dividend}.ceilDiv(${divisor}))")

            assertEquals(
                dividend.ceilDiv(divisor),
                Math.ceilDiv(dividend, divisor),
                "Failed for dividend: $dividend, divisor: $divisor"
            )
        }
    }

    @Test
    fun testCeilDivLong() {
        repeat(10) {
            val dividend = SecureRandom.readLong()
            val divisor = SecureRandom.readShort().toLong()

            //println("assertEquals(${dividend.ceilDiv(divisor)}, ${dividend}.ceilDiv(${divisor}))")

            assertEquals(
                dividend.ceilDiv(divisor),
                Math.ceilDiv(dividend, divisor),
                "Failed for dividend: $dividend, divisor: $divisor"
            )
        }
    }
}