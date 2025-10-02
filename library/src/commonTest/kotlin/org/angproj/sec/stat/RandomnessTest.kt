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
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random
import kotlin.test.*

class RandomMock : Randomness {
    override fun readByte(): Byte = Random.nextBits(8).toByte()
    override fun readShort(): Short = Random.nextBits(16).toShort()
    override fun readInt(): Int = Random.nextInt()
    override fun readBytes(data: ByteArray, offset: Int, size: Int) {
        Random.nextBytes(data, offset, offset + size)
    }

    override fun readDouble(): Double {
        // Mocking the generation of a uniform random double in [0.0, 1.0)
        return Random.nextBits(32).toUnitFraction().toDouble()
    }
}

class RandomnessTest {
    private lateinit var randomness: Randomness

    @BeforeTest
    fun setup() {
        randomness = RandomMock()
    }

    @Test
    fun testReadByte() {
        repeat(100) {
            val byte = randomness.readByte()
            assertContains(Byte.MIN_VALUE.toInt()..Byte.MAX_VALUE.toInt(), byte.toInt())
        }
    }

    @Test
    fun testReadUByte() {
        repeat(100) {
            val uByte = randomness.readUByte()
            assertContains(UByte.MIN_VALUE.toInt()..UByte.MAX_VALUE.toInt(), uByte.toInt())
        }
    }

    @Test
    fun testReadShort() {
        repeat(100) {
            val short = randomness.readShort()
            assertContains(Short.MIN_VALUE.toInt()..Short.MAX_VALUE.toInt(), short.toInt())
        }
    }

    @Test
    fun testReadUShort() {
        repeat(100) {
            val uShort = randomness.readUShort()
            assertContains(UShort.MIN_VALUE.toInt()..UShort.MAX_VALUE.toInt(), uShort.toInt())
        }
    }

    @Test
    fun testReadInt() {
        repeat(100) {
            val int = randomness.readInt()
            assertContains(Int.MIN_VALUE..Int.MAX_VALUE, int)
        }
    }

    @Test
    fun testReadUInt() {
        repeat(100) {
            val uInt = randomness.readUInt()
            assertContains(UInt.MIN_VALUE.toLong()..UInt.MAX_VALUE.toLong(), uInt.toLong())
        }
    }

    @Test
    fun testReadLong() {
        repeat(100) {
            val long = randomness.readLong()
            assertContains(Long.MIN_VALUE..Long.MAX_VALUE, long)
        }
    }

    @Test
    fun testReadULong() {
        repeat(100) {
            val uLong = randomness.readULong()
            assertTrue(uLong >= ULong.MIN_VALUE && uLong <= ULong.MAX_VALUE, "ULong must be in [${ULong.MIN_VALUE}, ${ULong.MAX_VALUE}]")
        }
    }

    @Test
    fun testReadFloat() {
        repeat(100) {
            val float = randomness.readFloat()
            assertContains(0.0f..1.0f, float)
            assertTrue(float in 0.0f..<1.0f, "Float must be in [0.0, 1.0)")
        }
    }

    @Test
    fun testReadDouble() {
        repeat(100) {
            val double = randomness.readDouble()
            assertContains(0.0..1.0, double)
            assertTrue(double in 0.0..<1.0, "Double must be in [0.0, 1.0)")
        }
    }

    @Test
    fun testReadBytes() {
        val size = 128
        val byteArray = ByteArray(size)
        randomness.readBytes(byteArray)
        assertEquals(size, byteArray.size)
        assertTrue(byteArray.all { it in Byte.MIN_VALUE..Byte.MAX_VALUE })
    }

    @Test
    fun testReadBytesWithOffset() {
        val size = 128
        val offset = 10
        val byteArray = ByteArray(size + offset)
        val original = byteArray.copyOf()
        randomness.readBytes(byteArray, offset, size)
        // Check that bytes before offset are unchanged
        assertTrue(byteArray.sliceArray(0 until offset).contentEquals(original.sliceArray(0 until offset)))
        // Check that bytes in range are valid
        assertTrue(byteArray.sliceArray(offset until offset + size).all { it in Byte.MIN_VALUE..Byte.MAX_VALUE })
    }

    @Test
    fun testReadUniform() {
        repeat(100) {
            val min = 1.0
            val max = 500.0
            val double = randomness.readUniform(min, max)
            assertContains(min..max, double)
            assertTrue(double in min..<max, "Uniform must be in [$min, $max)")
        }
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readUniform(500.0, 1.0) }
    }

    @Test
    fun testReadDiscreteUniform() {
        repeat(100) {
            val min = 1
            val max = 500
            val int = randomness.readDiscreteUniform(min, max)
            assertContains(min..max, int)
        }
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readDiscreteUniform(500, 1) }
    }

    @Test
    fun testReadNormal() {
        val mean = 0.0
        val stdDev = 1.0
        val samples = (0 until 1000).map { randomness.readGaussian(mean, stdDev) }
        // Check most values are within ±3 standard deviations (covers ~99.7% of normal distribution)
        assertTrue(samples.count { it in mean - 3 * stdDev..mean + 3 * stdDev }.toDouble() / samples.size > 0.95)
        // Estimate mean and variance
        val sampleMean = samples.average()
        val sampleVariance = samples.map { (it - sampleMean).pow(2) }.average()
        assertTrue(abs(sampleMean - mean) < 0.1, "Sample mean $sampleMean too far from $mean")
        assertTrue(abs(sampleVariance - stdDev * stdDev) < 0.5, "Sample variance $sampleVariance too far from ${stdDev * stdDev}")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readGaussian(0.0, 0.0) }
    }

    @Test
    fun testReadExponential() {
        val lambda = 1.0
        val samples = (0 until 1000).map { randomness.readExponential(lambda) }
        assertTrue(samples.all { it >= 0.0 }, "Exponential must be non-negative")
        // Estimate mean (should be 1/lambda)
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - 1.0 / lambda) < 0.1, "Sample mean $sampleMean too far from ${1.0 / lambda}")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readExponential(0.0) }
    }

    @Test
    fun testReadBinomial() {
        val n = 10
        val p = 0.5
        repeat(100) {
            val int = randomness.readBinomial(n, p)
            assertContains(0..n, int)
        }
        // Estimate mean (should be n * p)
        val samples = (0 until 1000).map { randomness.readBinomial(n, p).toDouble() }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - n * p) < 0.5, "Sample mean $sampleMean too far from ${n * p}")
        // Test invalid inputs
        assertFailsWith<IllegalArgumentException> { randomness.readBinomial(-1, 0.5) }
        assertFailsWith<IllegalArgumentException> { randomness.readBinomial(10, -0.1) }
        assertFailsWith<IllegalArgumentException> { randomness.readBinomial(10, 1.1) }
    }

    @Test
    fun testReadPoisson() {
        val lambda = 4.0
        repeat(100) {
            val int = randomness.readPoisson(lambda)
            assertTrue(int >= 0, "Poisson must be non-negative")
        }
        // Estimate mean (should be lambda)
        val samples = (0 until 1000).map { randomness.readPoisson(lambda).toDouble() }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - lambda) < 0.5, "Sample mean $sampleMean too far from $lambda")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readPoisson(0.0) }
    }

    @Test
    fun testReadGeometric() {
        val p = 0.5
        repeat(100) {
            val int = randomness.readGeometric(p)
            assertTrue(int >= 1, "Geometric must be at least 1")
        }
        // Estimate mean (should be 1/p)
        val samples = (0 until 1000).map { randomness.readGeometric(p).toDouble() }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - 1.0 / p) < 0.5, "Sample mean $sampleMean too far from ${1.0 / p}")
        // Test invalid inputs
        assertFailsWith<IllegalArgumentException> { randomness.readGeometric(0.0) }
        assertFailsWith<IllegalArgumentException> { randomness.readGeometric(1.1) }
    }

    @Test
    fun testReadNegativeBinomial() {
        val r = 5
        val p = 0.5
        repeat(100) {
            val int = randomness.readNegativeBinomial(r, p)
            assertTrue(int >= 0, "Negative Binomial must be non-negative")
        }
        // Estimate mean (should be r * (1-p)/p)
        val samples = (0 until 1000).map { randomness.readNegativeBinomial(r, p).toDouble() }
        val sampleMean = samples.average()
        val expectedMean = r * (1.0 - p) / p
        assertTrue(abs(sampleMean - expectedMean) < 0.5, "Sample mean $sampleMean too far from $expectedMean")
        // Test invalid inputs
        assertFailsWith<IllegalArgumentException> { randomness.readNegativeBinomial(0, 0.5) }
        assertFailsWith<IllegalArgumentException> { randomness.readNegativeBinomial(5, 0.0) }
    }

    @Test
    fun testReadGamma() {
        val shape = 2.0
        val scale = 2.0
        repeat(100) {
            val double = randomness.readGamma(shape, scale)
            assertTrue(double >= 0.0, "Gamma must be non-negative")
        }
        // Estimate mean (should be shape * scale)
        val samples = (0 until 1000).map { randomness.readGamma(shape, scale) }
        val sampleMean = samples.average()
        val expectedMean = shape * scale
        assertTrue(abs(sampleMean - expectedMean) < 0.5, "Sample mean $sampleMean too far from $expectedMean")
        // Test invalid inputs
        assertFailsWith<IllegalArgumentException> { randomness.readGamma(0.0, 2.0) }
        assertFailsWith<IllegalArgumentException> { randomness.readGamma(2.0, 0.0) }
    }

    @Test
    fun testReadBeta() {
        val alpha = 2.0
        val beta = 5.0
        repeat(100) {
            val double = randomness.readBeta(alpha, beta)
            assertContains(0.0..1.0, double)
            assertTrue(double >= 0.0 && double <= 1.0, "Beta must be in [0.0, 1.0]")
        }
        // Estimate mean (should be alpha / (alpha + beta))
        val samples = (0 until 1000).map { randomness.readBeta(alpha, beta) }
        val sampleMean = samples.average()
        val expectedMean = alpha / (alpha + beta)
        assertTrue(abs(sampleMean - expectedMean) < 0.1, "Sample mean $sampleMean too far from $expectedMean")
        // Test invalid inputs
        assertFailsWith<IllegalArgumentException> { randomness.readBeta(0.0, 5.0) }
        assertFailsWith<IllegalArgumentException> { randomness.readBeta(2.0, 0.0) }
    }

    @Test
    fun testReadChiSquare() {
        val df = 4
        repeat(100) {
            val double = randomness.readChiSquare(df)
            assertTrue(double >= 0.0, "Chi-Square must be non-negative")
        }
        // Estimate mean (should be df)
        val samples = (0 until 1000).map { randomness.readChiSquare(df) }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - df) < 0.5, "Sample mean $sampleMean too far from $df")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readChiSquare(0) }
    }

    @Test
    fun testReadStudentT() {
        val df = 10
        repeat(100) {
            val double = randomness.readStudentT(df)
            assertTrue(double.isFinite(), "Student's t must be finite")
        }
        // Estimate mean (should be 0 for df > 1)
        val samples = (0 until 1000).map { randomness.readStudentT(df) }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean) < 0.1, "Sample mean $sampleMean too far from 0")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readStudentT(0) }
    }

    @Test
    fun testReadBernoulli() {
        val p = 0.5
        repeat(100) {
            val int = randomness.readBernoulli(p)
            assertContains(0..1, int, "Bernoulli must be 0 or 1")
        }
        // Estimate mean (should be p)
        val samples = (0 until 1000).map { randomness.readBernoulli(p).toDouble() }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - p) < 0.1, "Sample mean $sampleMean too far from $p")
        // Test invalid inputs
        assertFailsWith<IllegalArgumentException> { randomness.readBernoulli(-0.1) }
        assertFailsWith<IllegalArgumentException> { randomness.readBernoulli(1.1) }
    }

    @Test
    fun testReadLaplace() {
        val mean = 0.0
        val scale = 1.0
        repeat(100) {
            val double = randomness.readLaplace(mean, scale)
            assertTrue(double.isFinite(), "Laplace must be finite")
        }
        // Estimate mean (should be mean)
        val samples = (0 until 1000).map { randomness.readLaplace(mean, scale) }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - mean) < 0.1, "Sample mean $sampleMean too far from $mean")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readLaplace(0.0, 0.0) }
    }

    @Test
    fun testReadLogistic() {
        val mean = 0.0
        val scale = 1.0
        repeat(100) {
            val double = randomness.readLogistic(mean, scale)
            assertTrue(double.isFinite(), "Logistic must be finite")
        }
        // Estimate mean (should be mean)
        val samples = (0 until 1000).map { randomness.readLogistic(mean, scale) }
        val sampleMean = samples.average()
        assertTrue(abs(sampleMean - mean) < 0.1, "Sample mean $sampleMean too far from $mean")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readLogistic(0.0, 0.0) }
    }

    @Test
    fun testReadCauchy() {
        val x0 = 0.0
        val gamma = 1.0
        repeat(100) {
            val double = randomness.readCauchy(x0, gamma)
            assertTrue(double.isFinite() || double.isInfinite(), "Cauchy must be finite or infinite")
        }
        // Cauchy has undefined mean, so we check median approximately
        val samples = (0 until 1000).map { randomness.readCauchy(x0, gamma) }.filter { it.isFinite() }
        val sampleMedian = samples.sorted()[samples.size / 2]
        assertTrue(abs(sampleMedian - x0) < 1.0, "Sample median $sampleMedian too far from $x0")
        // Test invalid input
        assertFailsWith<IllegalArgumentException> { randomness.readCauchy(0.0, 0.0) }
    }

    @Test
    fun testReadF() {
        val d1 = 5
        val d2 = 10
        repeat(100) {
            val double = randomness.readFDist(d1, d2)
            assertTrue(double >= 0.0 || double.isInfinite(), "F-distribution must be non-negative or infinite")
        }
        // Estimate mean (should be d2/(d2-2) for d2 > 2)
        val samples = (0 until 1000).map { randomness.readFDist(d1, d2) }.filter { it.isFinite() }
        val sampleMean = samples.average()
        val expectedMean = d2.toDouble() / (d2 - 2)
        assertTrue(abs(sampleMean - expectedMean) < 0.5, "Sample mean $sampleMean too far from $expectedMean")
        // Test invalid inputs
        assertFailsWith<IllegalArgumentException> { randomness.readFDist(0, 10) }
        assertFailsWith<IllegalArgumentException> { randomness.readFDist(5, 0) }
    }
}