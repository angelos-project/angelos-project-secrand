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

import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.TimeSource

public class JitterTest {
    private val start = TimeSource.Monotonic.markNow();

    public fun nextJitter(bits: Int): Int {
        require(bits in 1..32)
        val recent = start.elapsedNow();
        val nano: Double = 1.0 / recent.inWholeNanoseconds
        val micro: Double = 1.0 - (1.0 / recent.inWholeMicroseconds)

        val nanoBits: Long = sin(nano).toRawBits()
        val microBits: Long = cos(micro).toRawBits()
        val comboBits: Long = atan2(nano, micro).toRawBits()

        val trimmedNanoBits: Long = nanoBits shl nanoBits.countLeadingZeroBits()
        val trimmedMicroBits: Long = microBits ushr microBits.countTrailingZeroBits()

        val mixedBits: Long = (trimmedNanoBits xor trimmedMicroBits) xor comboBits.rotateLeft(41)

        return ((mixedBits ushr 32).toInt() xor (mixedBits and 0xffffffff).toInt()) ushr (32 - bits)
    }
}

public class JitterAvalancheObject(obj: JitterTest): AvalancheObject<JitterTest>(obj) {
    public override val bufferSize: Int
        get() = 4

    public override val digestSize: Int
        get() = 4 * 8

    public override fun digest(data: LongArray) {
        repeat(data.size) {
            data[it] = obj.nextJitter(32).toLong() shl 32 xor obj.nextJitter(32).toLong()

        }
    }
}

public class JitterMonteObject(obj: JitterTest): MonteObject<JitterTest>(obj) {
    public override fun readNextDouble(data: DoubleArray) {
        repeat(data.size) {
            data[it] = (obj.nextJitter(32) / (1L shl 31).toDouble()).absoluteValue
        }
    }

    public override val bufferSize: Int
        get() = 16
}

public fun main() {
    val monteCarlo = MonteCarlo(10_000_000) {
        JitterMonteObject(JitterTest())
    }
    monteCarlo.calculateData()
    println("Monte Carlo")
    println(monteCarlo)

    val avalancheEffect = AvalancheEffect(10_000_000) {
        JitterAvalancheObject(JitterTest())
    }
    avalancheEffect.calculateData()
    println("Avalanche Effect")
    println(avalancheEffect)

    repeat(100) {
        println(Uuid())
    }
}