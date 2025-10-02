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

import org.angproj.sec.util.toUnitFraction
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Interface defining methods for generating random numbers and sampling from various statistical distributions.
 * Implementations should provide a source of randomness and ensure that the generated values are uniformly
 * distributed over their respective ranges.
 *
 * The methods for reading primitive types (Byte, Short, Int, Long, Float, Double) are designed to return values
 * normalized to their full range. For example, readByte() returns a Byte in the range [-128, 127], and readFloat()
 * returns a Float in the range [0.0, 1.0).
 *
 * The interface also includes methods for sampling from common statistical distributions such as uniform,
 * normal (Gaussian), exponential, binomial, Poisson, geometric, negative binomial, gamma, beta, chi-square,
 * Student's t-distribution, Bernoulli, Laplace, Logistic, Cauchy, and F-distribution.
 *
 * Each distribution method includes parameters to define the characteristics of the distribution (e.g., mean,
 * standard deviation for normal distribution) and returns a random value sampled from that distribution.
 *
 * Implementations should ensure that the random values generated are statistically sound and suitable for
 * applications requiring randomness, such as simulations, cryptography, and statistical sampling.
 */
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
    public fun readLong(): Long = (readInt() shl 32).toLong() or (readInt().toLong() and 0xFFFFFFFFL)

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
     * Generates a random double from a continuous uniform distribution over [min, max).
     *
     * @param min The inclusive lower bound of the distribution.
     * @param max The exclusive upper bound of the distribution.
     * @return A random double in [min, max).
     * @throws IllegalArgumentException if min >= max.
     */
    public fun readUniform(min: Double, max: Double): Double {
        require(min < max) { "min must be less than max" }
        return min + (max - min) * readDouble()
    }

    /**
     * Generates a random integer from a discrete uniform distribution over [min, max].
     *
     * @param min The inclusive lower bound of the distribution.
     * @param max The inclusive upper bound of the distribution.
     * @return A random integer in [min, max].
     * @throws IllegalArgumentException if min > max.
     */
    public fun readDiscreteUniform(min: Int, max: Int): Int {
        require(min <= max) { "min must be less than or equal to max" }
        return min + (readDouble() * (max - min + 1)).toInt()
    }

    /**
     * Generates a random double from a normal (Gaussian) distribution.
     *
     * @param mean The mean of the distribution.
     * @param stdDev The standard deviation of the distribution.
     * @return A random double from N(mean, stdDev^2).
     * @throws IllegalArgumentException if stdDev <= 0.
     */
    public fun readGaussian(mean: Double, stdDev: Double): Double {
        require(stdDev > 0) { "Standard deviation must be positive" }
        val u1 = readDouble()
        val u2 = readDouble()
        val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
        return mean + stdDev * z0
    }

    /**
     * Generates a random double from a standard normal (Gaussian) distribution N(0, 1).
     *
     * @return A random double from N(0, 1).
     */
    public fun readGaussian(): Double = readGaussian(0.0, 1.0)

    /**
     * Generates a random double from an exponential distribution.
     *
     * @param lambda The rate parameter (1/mean) of the distribution.
     * @return A random double from Exp(lambda).
     * @throws IllegalArgumentException if lambda <= 0.
     */
    public fun readExponential(lambda: Double): Double {
        require(lambda > 0) { "Lambda must be positive" }
        return -ln(1.0 - readDouble()) / lambda
    }

    /**
     * Generates a random integer from a binomial distribution.
     *
     * @param n The number of trials.
     * @param p The probability of success on each trial.
     * @return A random integer representing the number of successes.
     * @throws IllegalArgumentException if n < 0 or p is not in [0, 1].
     */
    public fun readBinomial(n: Int, p: Double): Int {
        require(n >= 0) { "Number of trials must be non-negative" }
        require(p in 0.0..1.0) { "Probability must be in [0, 1]" }
        var successes = 0
        for (i in 0 until n) {
            if (readDouble() < p) {
                successes++
            }
        }
        return successes
    }

    /**
     * Generates a random integer from a Poisson distribution.
     *
     * @param lambda The mean (and variance) of the distribution.
     * @return A random integer from Poisson(lambda).
     * @throws IllegalArgumentException if lambda <= 0.
     */
    public fun readPoisson(lambda: Double): Int {
        require(lambda > 0) { "Lambda must be positive" }
        // Knuth's algorithm for Poisson
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
     * Generates a random integer from a geometric distribution.
     *
     * @param p The probability of success on each trial.
     * @return A random integer representing the number of trials until the first success.
     * @throws IllegalArgumentException if p is not in (0, 1].
     */
    public fun readGeometric(p: Double): Int {
        require(p in 0.0..1.0 && p > 0) { "Probability must be in (0, 1]" }
        // Inverse CDF: ceil(ln(1 - U) / ln(1 - p))
        return ceil(ln(1.0 - readDouble()) / ln(1.0 - p)).toInt()
    }

    /**
     * Generates a random integer from a negative binomial distribution.
     *
     * @param r The number of successes until the experiment is stopped.
     * @param p The probability of success on each trial.
     * @return A random integer representing the number of failures until r successes.
     * @throws IllegalArgumentException if r <= 0 or p is not in (0, 1].
     */
    public fun readNegativeBinomial(r: Int, p: Double): Int {
        require(r > 0) { "Number of successes must be positive" }
        require(p in 0.0..1.0 && p > 0) { "Probability must be in (0, 1]" }
        // Sum of r geometric random variables
        var failures = 0
        for (i in 0 until r) {
            failures += readGeometric(p) - 1
        }
        return failures
    }

    /**
     * Generates a random double from a gamma distribution.
     *
     * @param shape The shape parameter (α) of the distribution.
     * @param scale The scale parameter (θ) of the distribution.
     * @return A random double from Gamma(shape, scale).
     * @throws IllegalArgumentException if shape <= 0 or scale <= 0.
     */
    public fun readGamma(shape: Double, scale: Double): Double {
        require(shape > 0) { "Shape must be positive" }
        require(scale > 0) { "Scale must be positive" }
        // For integer shape, sum exponential variates; for non-integer, use approximation
        if (shape.isWhole()) {
            var sum = 0.0
            for (i in 0 until shape.toInt()) {
                sum += readExponential(1.0 / scale)
            }
            return sum
        } else {
            // Marsaglia-Tsang approximation for non-integer shape
            val d = shape - 1.0 / 3.0
            val c = 1.0 / sqrt(9.0 * d)
            while (true) {
                val x = readGaussian()
                val v = (1.0 + c * x).pow(3)
                if (v > 0) {
                    val u = readDouble()
                    if (u < 1.0 - 0.0331 * x * x * x * x) return d * v * scale
                    if (ln(u) < 0.5 * x * x + d * (1.0 - v + ln(v))) return d * v * scale
                }
            }
        }
    }

    /**
     * Generates a random double from a beta distribution.
     *
     * @param alpha The first shape parameter (α) of the distribution.
     * @param beta The second shape parameter (β) of the distribution.
     * @return A random double from Beta(alpha, beta) in [0, 1].
     * @throws IllegalArgumentException if alpha <= 0 or beta <= 0.
     */
    public fun readBeta(alpha: Double, beta: Double): Double {
        require(alpha > 0) { "Alpha must be positive" }
        require(beta > 0) { "Beta must be positive" }
        // Use gamma variates: X / (X + Y) where X ~ Gamma(alpha, 1), Y ~ Gamma(beta, 1)
        val x = readGamma(alpha, 1.0)
        val y = readGamma(beta, 1.0)
        return x / (x + y)
    }

    /**
     * Generates a random double from a chi-square distribution.
     *
     * @param df The degrees of freedom.
     * @return A random double from ChiSquare(df).
     * @throws IllegalArgumentException if df <= 0.
     */
    public fun readChiSquare(df: Int): Double {
        require(df > 0) { "Degrees of freedom must be positive" }
        // Chi-square with df is Gamma(df/2, 2)
        return readGamma(df / 2.0, 2.0)
    }

    /**
     * Generates a random double from a Student's t-distribution.
     *
     * @param df The degrees of freedom.
     * @return A random double from t(df).
     * @throws IllegalArgumentException if df <= 0.
     */
    public fun readStudentT(df: Int): Double {
        require(df > 0) { "Degrees of freedom must be positive" }
        // t-distribution: Z / sqrt(Y / df) where Z ~ N(0,1), Y ~ ChiSquare(df)
        val z = readGaussian()
        val y = readChiSquare(df)
        return z / sqrt(y / df)
    }

    /**
     * Generates a random integer from a Bernoulli distribution.
     *
     * @param p The probability of success (returning 1).
     * @return A random integer (0 or 1) from Bernoulli(p).
     * @throws IllegalArgumentException if p is not in [0, 1].
     */
    public fun readBernoulli(p: Double): Int {
        require(p in 0.0..1.0) { "Probability must be in [0, 1]" }
        return if (readDouble() < p) 1 else 0
    }

    /**
     * Generates a random double from a Laplace distribution.
     *
     * @param mean The mean (location parameter) of the distribution.
     * @param scale The scale parameter (b) of the distribution.
     * @return A random double from Laplace(mean, scale).
     * @throws IllegalArgumentException if scale <= 0.
     */
    public fun readLaplace(mean: Double, scale: Double): Double {
        require(scale > 0) { "Scale must be positive" }
        val u = readDouble() - 0.5
        return mean - scale * sign(u) * ln(1.0 - 2.0 * abs(u))
    }

    /**
     * Generates a random double from a standard Laplace distribution Laplace(0, 1).
     *
     * @return A random double from Laplace(0, 1).
     */
    public fun readLaplace(): Double = readLaplace(0.0, 1.0)

    /**
     * Generates a random double from a Logistic distribution.
     *
     * @param mean The mean (location parameter) of the distribution.
     * @param scale The scale parameter (s) of the distribution.
     * @return A random double from Logistic(mean, scale).
     * @throws IllegalArgumentException if scale <= 0.
     */
    public fun readLogistic(mean: Double, scale: Double): Double {
        require(scale > 0) { "Scale must be positive" }
        val u = readDouble()
        return mean + scale * ln(u / (1.0 - u))
    }

    /**
     * Generates a random double from a standard Logistic distribution Logistic(0, 1).
     *
     * @return A random double from Logistic(0, 1).
     */
    public fun readLogistic(): Double = readLogistic(0.0, 1.0)

    /**
     * Generates a random double from a Cauchy distribution.
     *
     * @param x0 The location parameter (median) of the distribution.
     * @param gamma The scale parameter of the distribution.
     * @return A random double from Cauchy(x0, gamma).
     * @throws IllegalArgumentException if gamma <= 0.
     */
    public fun readCauchy(x0: Double, gamma: Double): Double {
        require(gamma > 0) { "Gamma must be positive" }
        val u = readDouble()
        return x0 + gamma * tan(PI * (u - 0.5))
    }

    /**
     * Generates a random double from a standard Cauchy distribution Cauchy(0, 1).
     *
     * @return A random double from Cauchy(0, 1).
     */
    public fun readCauchy(): Double = readCauchy(0.0, 1.0)

    /**
     * Generates a random double from an F-distribution.
     *
     * @param d1 The degrees of freedom for the numerator.
     * @param d2 The degrees of freedom for the denominator.
     * @return A random double from F(d1, d2).
     * @throws IllegalArgumentException if d1 <= 0 or d2 <= 0.
     */
    public fun readFDist(d1: Int, d2: Int): Double {
        require(d1 > 0) { "Numerator degrees of freedom must be positive" }
        require(d2 > 0) { "Denominator degrees of freedom must be positive" }
        val u = readChiSquare(d1) / d1
        val v = readChiSquare(d2) / d2
        return if (v == 0.0) Double.POSITIVE_INFINITY else u / v
    }

    /**
     * Extension to check if a double is a whole number.
     */
    private fun Double.isWhole(): Boolean = this == floor(this)
}