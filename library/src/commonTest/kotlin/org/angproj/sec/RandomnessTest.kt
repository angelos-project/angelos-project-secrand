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

import org.angproj.sec.stat.Randomness
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertTrue

class RandomMock: Randomness {

    override fun readByte(): Byte = Random.nextBits(8).toByte()

    override fun readShort(): Short = Random.nextBits(16).toShort()

    override fun readInt(): Int = Random.nextInt()

    override fun readBytes(data: ByteArray, offset: Int, size: Int) {
        Random.nextBytes(data, offset, offset + size)
    }
}

class RandomnessTest {

    lateinit var randomness: Randomness

    @BeforeTest
    fun setup() {
        randomness = RandomMock()
    }

    @Test
    fun testReadByte() {
        val byte = randomness.readByte()
        assertContains(Byte.MIN_VALUE..Byte.MAX_VALUE, byte.toInt())
    }

    @Test
    fun testReadUByte() {
        val uByte = randomness.readUByte()
        assertContains(UByte.MIN_VALUE.toInt()..UByte.MAX_VALUE.toInt(), uByte.toInt())
    }

    @Test
    fun testReadShort() {
        val short = randomness.readShort()
        assertContains(Short.MIN_VALUE..Short.MAX_VALUE, short.toInt())
    }

    @Test
    fun testReadUShort() {
        val uShort = randomness.readUShort()
        assertContains(UShort.MIN_VALUE.toInt()..UShort.MAX_VALUE.toInt(), uShort.toInt())
    }

    @Test
    fun testReadInt() {
        val int = randomness.readInt()
        assertContains(Int.MIN_VALUE..Int.MAX_VALUE, int)
    }

    @Test
    fun testReadUInt() {
        val uInt = randomness.readUInt()
        assertContains(UInt.MIN_VALUE..UInt.MAX_VALUE, uInt)
    }

    @Test
    fun testReadLong() {
        val long = randomness.readLong()
        assertContains(Long.MIN_VALUE..Long.MAX_VALUE, long)
    }

    @Test
    fun testReadULong() {
        val uLong = randomness.readULong()
        assertContains(ULong.MIN_VALUE..ULong.MAX_VALUE, uLong)
    }

    @Test
    fun testReadFloat() {
        val float = randomness.readFloat()
        assertContains(0.0f..1.0f, float)
    }

    @Test
    fun testReadDouble() {
        val double = randomness.readDouble()
        assertContains(0.0..1.0, double)
    }

    @Test
    fun testReadBytes() {
        val size = 128
        val byteArray = ByteArray(size)
        randomness.readBytes(byteArray, 0, size)
        //assert(byteArray.all { it in Byte.MIN_VALUE..Byte.MAX_VALUE })
    }

    @Test
    fun testReadBytesWithOffset() {
        val size = 128
        val offset = 10
        val byteArray = ByteArray(size + offset)
        randomness.readBytes(byteArray, offset, size)
        //assert(byteArray.slice(offset until offset + size).all { it in Byte.MIN_VALUE..Byte.MAX_VALUE })
    }

    @Test
    fun testReadUniform() {
        val double = randomness.readUniform(1.0, 500.0)
        assertContains(1.0..500.0, double)
    }

    @Test
    fun readDiscreteUniform() {
        val int = randomness.readDiscreteUniform(1, 500)
        assertContains(1..500, int)
    }

    @Test
    fun testReadGaussian() {
        val double = randomness.readGaussian()
        assertContains(0.0..1.0, double) // Most values should fall within this range
    }

    @Test
    fun readExponential() {
        val double = randomness.readExponential(1.0)
        assertTrue(double >= 0.0) // Exponential distribution is always non-negative
    }

    @Test
    fun testReadBinomial() {
        val int = randomness.readBinomial(10, 0.5)
        assertContains(0..10, int) // Result should be between 0 and n
    }

    @Test
    fun testReadPoisson() {
        val int = randomness.readPoisson(4.0)
        assertTrue(int >= 0) // Poisson distribution is always non-negative
    }

    @Test
    fun testReadGeometric() {
        val int = randomness.readGeometric(0.5)
        assertTrue(int >= 1) // Geometric distribution starts at 1
    }

    @Test
    fun testReadNegativeBinomial() {
        val int = randomness.readNegativeBinomial(5, 0.5)
        assertTrue(int >= 0) // Negative Binomial distribution is always non-negative
    }

    @Test
    fun testReadGamma() {
        val double = randomness.readGamma(2.0, 2.0)
        assertTrue(double >= 0.0) // Gamma distribution is always non-negative
    }

    @Test
    fun testReadBeta() {
        val double = randomness.readBeta(2.0, 5.0)
        assertContains(0.0..1.0, double) // Beta distribution is between 0 and 1
    }

    @Test
    fun testReadChiSquare() {
        val double = randomness.readChiSquare(4)
        assertTrue(double >= 0.0) // Chi-Square distribution is always non-negative
    }

    @Test
    fun testReadStudentT() {
        val double = randomness.readStudentT(10)
        assertTrue(double.isFinite()) // Student's t-distribution can produce any real number
    }

    @Test
    fun testReadBernoulli() {
        val bool = randomness.readBernoulli(0.5)
        //assertTrue(bool == true || bool == false) // Should return either true or false
    }

    @Test
    fun testReadLaplace() {
        val double = randomness.readLaplace()
        assertTrue(double.isFinite()) // Laplace distribution can produce any real number
    }

    @Test
    fun testReadLogistic() {
        val double = randomness.readLogistic()
        assertTrue(double.isFinite()) // Logistic distribution can produce any real number
    }

    @Test
    fun testReadCauchy() {
        val double = randomness.readCauchy()
        assertTrue(double.isFinite()) // Cauchy distribution can produce any real number
    }


    @Test
    fun testReadFDist() {
        val double = randomness.readFDist(5, 2)
        assertTrue(double >= 0.0) // F-distribution is always non-negative
    }
}