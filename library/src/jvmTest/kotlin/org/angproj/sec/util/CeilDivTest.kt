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

    @Test
    fun testFixAndTrix() {
        for (i in 0..15) {
            // Map linear index to 4x4 matrix coordinates
            val row = i / 4
            val col = i % 4
            // Map neighbor indices to linear array
            val up = ((row + 1) % 4) * 4 + col // (row + 1) % 4, col
            val down = ((row - 1 + 4) % 4) * 4 + col // (row - 1) % 4, col
            val right = row * 4 + ((col + 1) % 4) // row, (col + 1) % 4
            val left = row * 4 + ((col - 1 + 4) % 4) // row, (col - 1) % 4
            // Mix with neighbors (up, down, left, right, wrapping around)
            println("val sponge$i = diffuse<Unit>($i, $up, $down, $right, $left)")
        }

        for(i in 0..15) {
            // Step 2: Non-linear transformation (S-box-like substitution)
            println("sponge[$i] = confuse<Unit>(sponge$i)")
            //state[i] = x xor (x shl 17) xor (x ushr 23)
        }
    }
}