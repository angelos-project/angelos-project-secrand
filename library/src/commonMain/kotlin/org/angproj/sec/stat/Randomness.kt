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
package org.angproj.sec.stat

import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.toUnitFraction
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

public interface Randomness {

    /**
     * Reads a byte from the random source.
     * The value is normalized to the range [-128, 127].
     *
     * @return A byte in the range [-128, 127].
     */
    public fun readByte(): Byte = readInt().toByte()

    /**
     * Reads an unsigned byte from the random source.
     * The value is normalized to the range [0, 255].
     *
     * @return An unsigned byte in the range [0, 255].
     */
    public fun readUByte(): UByte = readInt().toUByte()

    /**
     * Reads a short integer from the random source.
     * The value is normalized to the range [-32768, 32767].
     *
     * @return A short integer in the range [-32768, 32767].
     */
    public fun readShort(): Short = readInt().toShort()

    /**
     * Reads an unsigned short integer from the random source.
     * The value is normalized to the range [0, 65535].
     *
     * @return An unsigned short integer in the range [0, 65535].
     */
    public fun readUShort(): UShort = readInt().toUShort()

    /**
     * Reads an integer from the random source.
     * The value is normalized to the range [-2147483648, 2147483647].
     *
     * @return An integer in the range [-2147483648, 2147483647].
     */
    public fun readInt(): Int

    /**
     * Reads an unsigned integer from the random source.
     * The value is normalized to the range [0, 4294967295].
     *
     * @return An unsigned integer in the range [0, 4294967295].
     */
    public fun readUInt(): UInt = readInt().toUInt()

    /**
     * Reads a long integer from the random source.
     * The value is normalized to the range [-9223372036854775808, 9223372036854775807].
     *
     * @return A long integer in the range [-9223372036854775808, 9223372036854775807].
     */
    public fun readLong(): Long = (readInt() shl TypeSize.intBits).toLong() or (readInt().toLong() and 0xFFFFFFFFL)

    /**
     * Reads an unsigned long integer from the random source.
     * The value is normalized to the range [0, 18446744073709551615].
     *
     * @return An unsigned long integer in the range [0, 18446744073709551615].
     */
    public fun readULong(): ULong = readLong().toULong()

    /**
     * Reads a single-precision floating-point number from the random source.
     * The value is normalized to the range [0.0, 1.0) by dividing by 2^31.
     *
     * @return A single-precision floating-point number in the range [0.0, 1.0).
     */
    public fun readFloat(): Float = readInt().toUnitFraction()

    /**
     * Reads a double-precision floating-point number from the random source.
     * The value is normalized to the range [0.0, 1.0) by dividing by 2^63.
     *
     * @return A double-precision floating-point number in the range [0.0, 1.0).
     */
    public fun readDouble(): Double = readLong().toUnitFraction()

    /**
     * Generates a random value from a standard normal (Gaussian) distribution
     * using the Box-Muller transform.
     *
     * @return A Double sampled from N(0, 1).
     */
    public fun readGaussian(): Double {
        val u1 = readDouble()
        val u2 = readDouble()
        val r = sqrt(-2.0 * ln(u1))
        val theta = 2.0 * PI * u2
        return r * cos(theta)
    }

    public fun readExponential(lambda: Double): Double {
        val u = readDouble()
        return -ln(1 - u) / lambda
    }

    public fun readPoisson(lambda: Double): Int {
        val L = exp(-lambda)
        var k = 0
        var p = 1.0
        do {
            k++
            p *= readDouble()
        } while (p > L)
        return k - 1
    }

    public fun readGamma(shape: Double, scale: Double): Double {
        if (shape < 1) {
            val u = readDouble()
            return readGamma(1.0 + shape, scale) * u.pow(1.0 / shape)
        }
        val d = shape - 1.0 / 3.0
        val c = 1.0 / sqrt(9.0 * d)
        while (true) {
            var x: Double
            var v: Double
            do {
                x = readGaussian()
                v = 1.0 + c * x
            } while (v <= 0)
            v = v * v * v
            val u = readDouble()
            if (u < 1.0 - 0.0331 * x * x * x * x) return scale * d * v
            if (ln(u) < 0.5 * x * x + d * (1.0 - v + ln(v))) return scale * d * v
        }
    }

    public fun readBeta(a: Double, b: Double): Double {
        val x = readGamma(a, 1.0)
        val y = readGamma(b, 1.0)
        return x / (x + y)
    }

    public fun readUniform(a: Double, b: Double): Double {
        return a + (b - a) * readDouble()
    }

    public fun readBernoulli(p: Double): Boolean {
        return readDouble() < p
    }

    public fun readBinomial(n: Int, p: Double): Int {
        var successes = 0
        repeat(n) {
            if (readBernoulli(p)) successes++
        }
        return successes
    }

}