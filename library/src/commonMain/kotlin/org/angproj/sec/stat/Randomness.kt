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
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

public interface Randomness {

    /**
     * Reads a byte from the random source.
     * The value is normalized to the range [-128, 127].
     *
     * @return A byte in the range [-128, 127].
     */
    public fun readByte(): Byte

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
    public fun readShort(): Short

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
     * Reads bytes into a ByteArray from the random source,
     * starting at the specified offset and ending at the specified size.
     *
     * @param data The ByteArray to fill with random bytes.
     * @param offset The starting index in the ByteArray to write to.
     * @param size The number of bytes to read. Defaults to the size of the ByteArray.
     */
    public fun readBytes(data: ByteArray, offset: Int = 0, size: Int = data.size)

    /**
     * Reads bytes into a ByteArray from the random source.
     *
     * @param data The ByteArray to fill with random bytes.
     */
    public fun readBytes(data: ByteArray): Unit = readBytes(data, 0, data.size)

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

    /**
     * Generates a random value from an exponential distribution
     * with the specified rate parameter (lambda).
     *
     * @param lambda The rate parameter of the exponential distribution (λ > 0).
     * @return A Double sampled from Exp(λ).
     */
    public fun readExponential(lambda: Double): Double {
        val u = readDouble()
        return -ln(1 - u) / lambda
    }

    /**
     * Generates a random value from a Poisson distribution
     * with the specified rate parameter (lambda).
     *
     * @param lambda The rate parameter of the Poisson distribution (λ > 0).
     * @return An Int sampled from Poisson(λ).
     */
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

    /**
     * Generates a random value from a Gamma distribution
     * with the specified shape and scale parameters.
     *
     * @param shape The shape parameter of the Gamma distribution (k > 0).
     * @param scale The scale parameter of the Gamma distribution (θ > 0).
     * @return A Double sampled from Gamma(k, θ).
     */
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

    /**
     * Generates a random value from a Beta distribution
     * with the specified shape parameters.
     *
     * @param a The first shape parameter of the Beta distribution (α > 0).
     * @param b The second shape parameter of the Beta distribution (β > 0).
     * @return A Double sampled from Beta(α, β).
     */
    public fun readBeta(a: Double, b: Double): Double {
        val x = readGamma(a, 1.0)
        val y = readGamma(b, 1.0)
        return x / (x + y)
    }

    /**
     * Generates a random value from a uniform distribution
     * within the specified range [a, b).
     *
     * @param a The lower bound of the uniform distribution.
     * @param b The upper bound of the uniform distribution.
     * @return A Double sampled from Uniform(a, b).
     */
    public fun readUniform(a: Double, b: Double): Double {
        return a + (b - a) * readDouble()
    }

    /**
     * Generates a random boolean value based on a Bernoulli distribution
     * with the specified probability of success (p).
     *
     * @param p The probability of success (0.0 <= p <= 1.0).
     * @return A Boolean sampled from Bernoulli(p).
     */
    public fun readBernoulli(p: Double): Boolean {
        return readDouble() < p
    }

    /**
     * Generates a random integer value from a Binomial distribution
     * with the specified number of trials (n) and probability of success (p).
     *
     * @param n The number of trials (n >= 0).
     * @param p The probability of success in each trial (0.0 <= p <= 1.0).
     * @return An Int sampled from Binomial(n, p).
     */
    public fun readBinomial(n: Int, p: Double): Int {
        var successes = 0
        repeat(n) {
            if (readBernoulli(p)) successes++
        }
        return successes
    }

    /**
     * Generates a random integer value from a Geometric distribution
     * with the specified probability of success (p).
     *
     * @param p The probability of success in each trial (0.0 < p <= 1.0).
     * @return An Int sampled from Geometric(p).
     */
    public fun readGeometric(p: Double): Int {
        return ceil(ln(1 - readDouble()) / ln(1 - p)).toInt()
    }

    /**
     * Generates a random integer value from a Negative Binomial distribution
     * with the specified number of successes (r) and probability of success (p).
     *
     * @param r The number of successes (r > 0).
     * @param p The probability of success in each trial (0.0 < p <= 1.0).
     * @return An Int sampled from NegativeBinomial(r, p).
     */
    public fun readNegativeBinomial(r: Int, p: Double): Int {
        var failures = 0
        var successes = 0
        while (successes < r) {
            if (readBernoulli(p)) {
                successes++
            } else {
                failures++
            }
        }
        return failures
    }

    /**
     * Generates a random value from a Cauchy distribution
     * with the specified location and scale parameters.
     *
     * @param location The location parameter of the Cauchy distribution (x0).
     * @param scale The scale parameter of the Cauchy distribution (γ > 0).
     * @return A Double sampled from Cauchy(x0, γ).
     */
    public fun readCauchy(location: Double, scale: Double): Double {
        val u = readDouble() - 0.5
        return location + scale * tan(PI * u)
    }
}