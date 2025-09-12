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
        val size = 4
        val width = 2
        for (i in 0 until size) {
            // Map linear index to 4x4 matrix coordinates
            val row = i / width
            val col = i % width
            // Map neighbor indices to linear array
            val up = ((row + 1) % width) * width + col // (row + 1) % 4, col
            val down = ((row - 1 + width) % width) * width + col // (row - 1) % 4, col
            val right = row * width + ((col + 1) % width) // row, (col + 1) % 4
            val left = row * width + ((col - 1 + width) % width) // row, (col - 1) % 4
            // Mix with neighbors (up, down, left, right, wrapping around)
            println("val sponge$i = diffuse<Unit>(sponge[$i], $up, $down, $right, $left)")
        }

        for(i in 0 until size) {
            // Step 2: Non-linear transformation (S-box-like substitution)
            println("sponge[$i] = confuse<Unit>(sponge$i)")
            //state[i] = x xor (x shl 17) xor (x ushr 23)
        }
    }
}