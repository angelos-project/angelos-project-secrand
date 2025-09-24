package org.angproj.sec.stat

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.mockito.Mockito
import kotlin.test.*

class RandomnessMockitoTest {

    private lateinit var randomMock: Randomness

    @BeforeTest
    fun setup() {
        randomMock = Mockito.mock(Randomness::class.java)
    }

    @Test
    fun testReadByte() {
        Mockito.`when`(randomMock.readByte()).thenReturn(42)
        val result = randomMock.readByte()
        assertEquals(42, result, "readByte should return mocked value")
        assertContains(Byte.MIN_VALUE.toInt()..Byte.MAX_VALUE.toInt(), result.toInt(), "readByte must be in valid range")
        Mockito.verify(randomMock, Mockito.times(1)).readByte()
    }

    @Test
    fun testReadUByte() {
        Mockito.`when`(randomMock.readUByte()).thenReturn(200u)
        val result = randomMock.readUByte()
        assertEquals(200u, result, "readUByte should return mocked value")
        assertContains(UByte.MIN_VALUE.toInt()..UByte.MAX_VALUE.toInt(), result.toInt(), "readUByte must be in valid range")
        Mockito.verify(randomMock, Mockito.times(1)).readUByte()
    }

    @Test
    fun testReadShort() {
        Mockito.`when`(randomMock.readShort()).thenReturn(1000)
        val result = randomMock.readShort()
        assertEquals(1000, result, "readShort should return mocked value")
        assertContains(Short.MIN_VALUE.toInt()..Short.MAX_VALUE.toInt(), result.toInt(), "readShort must be in valid range")
        Mockito.verify(randomMock, Mockito.times(1)).readShort()
    }

    @Test
    fun testReadUShort() {
        Mockito.`when`(randomMock.readUShort()).thenReturn(40000u)
        val result = randomMock.readUShort()
        assertEquals(40000u, result, "readUShort should return mocked value")
        assertContains(UShort.MIN_VALUE.toInt()..UShort.MAX_VALUE.toInt(), result.toInt(), "readUShort must be in valid range")
        Mockito.verify(randomMock, Mockito.times(1)).readUShort()
    }

    @Test
    fun testReadInt() {
        Mockito.`when`(randomMock.readInt()).thenReturn(123456)
        val result = randomMock.readInt()
        assertEquals(123456, result, "readInt should return mocked value")
        assertContains(Int.MIN_VALUE..Int.MAX_VALUE, result, "readInt must be in valid range")
        Mockito.verify(randomMock, Mockito.times(1)).readInt()
    }

    @Test
    fun testReadUInt() {
        Mockito.`when`(randomMock.readUInt()).thenReturn(3000000u)
        val result = randomMock.readUInt()
        assertEquals(3000000u, result, "readUInt should return mocked value")
        assertContains(UInt.MIN_VALUE.toLong()..UInt.MAX_VALUE.toLong(), result.toLong(), "readUInt must be in valid range")
        Mockito.verify(randomMock, Mockito.times(1)).readUInt()
    }

    @Test
    fun testReadLong() {
        Mockito.`when`(randomMock.readLong()).thenReturn(123456789L)
        val result = randomMock.readLong()
        assertEquals(123456789L, result, "readLong should return mocked value")
        assertContains(Long.MIN_VALUE..Long.MAX_VALUE, result, "readLong must be in valid range")
        Mockito.verify(randomMock, Mockito.times(1)).readLong()
    }

    @Test
    fun testReadULong() {
        Mockito.`when`(randomMock.readULong()).thenReturn(9876543210uL)
        val result = randomMock.readULong()
        assertEquals(9876543210uL, result, "readULong should return mocked value")
        assertTrue(result >= ULong.MIN_VALUE && result <= ULong.MAX_VALUE, "readULong must be in [${ULong.MIN_VALUE}, ${ULong.MAX_VALUE}]")
        Mockito.verify(randomMock, Mockito.times(1)).readULong()
    }

    @Test
    fun testReadFloat() {
        Mockito.`when`(randomMock.readFloat()).thenReturn(0.75f)
        val result = randomMock.readFloat()
        assertEquals(0.75f, result, "readFloat should return mocked value")
        assertContains(0.0f..1.0f, result, "readFloat must be in [0.0, 1.0)")
        assertTrue(result in 0.0f..<1.0f, "readFloat must be in [0.0, 1.0)")
        Mockito.verify(randomMock, Mockito.times(1)).readFloat()
    }

    @Test
    fun testReadDouble() {
        Mockito.`when`(randomMock.readDouble()).thenReturn(0.42)
        val result = randomMock.readDouble()
        assertEquals(0.42, result, "readDouble should return mocked value")
        assertContains(0.0..1.0, result, "readDouble must be in [0.0, 1.0)")
        assertTrue(result in 0.0..<1.0, "readDouble must be in [0.0, 1.0)")
        Mockito.verify(randomMock, Mockito.times(1)).readDouble()
    }

    @Test
    fun testReadBytes() {
        val size = 10
        val byteArray = ByteArray(size)
        Mockito.`when`(randomMock.readBytes(byteArray)).then {
            byteArray.fill(42)
            Unit
        }
        randomMock.readBytes(byteArray)
        assertTrue(byteArray.all { it == 42.toByte() }, "readBytes should fill array with mocked values")
        Mockito.verify(randomMock, Mockito.times(1)).readBytes(byteArray)
    }

    @Test
    fun testReadBytesWithOffset() {
        val size = 10
        val offset = 5
        val byteArray = ByteArray(size + offset)
        val original = byteArray.copyOf()
        Mockito.`when`(randomMock.readBytes(byteArray, offset, size)).then {
            for (i in offset until offset + size) {
                byteArray[i] = 42
            }
            Unit
        }
        randomMock.readBytes(byteArray, offset, size)
        assertTrue(byteArray.sliceArray(0 until offset).contentEquals(original.sliceArray(0 until offset)), "Bytes before offset should be unchanged")
        assertTrue(byteArray.sliceArray(offset until offset + size).all { it == 42.toByte() }, "Bytes in range should be mocked values")
        Mockito.verify(randomMock, Mockito.times(1)).readBytes(byteArray, offset, size)
    }

    @Test
    fun testReadUniform() {
        Mockito.`when`(randomMock.readUniform(1.0, 5.0)).thenReturn(3.0)
        val result = randomMock.readUniform(1.0, 5.0)
        assertEquals(3.0, result, "readUniform should return mocked value")
        assertContains(1.0..5.0, result, "readUniform must be in [1.0, 5.0)")
        Mockito.verify(randomMock, Mockito.times(1)).readUniform(1.0, 5.0)
        // Test invalid input
        Mockito.`when`(randomMock.readUniform(5.0, 1.0)).thenThrow(IllegalArgumentException("min must be less than max"))
        assertFailsWith<IllegalArgumentException> { randomMock.readUniform(5.0, 1.0) }
    }

    @Test
    fun testReadDiscreteUniform() {
        Mockito.`when`(randomMock.readDiscreteUniform(1, 10)).thenReturn(7)
        val result = randomMock.readDiscreteUniform(1, 10)
        assertEquals(7, result, "readDiscreteUniform should return mocked value")
        assertContains(1..10, result, "readDiscreteUniform must be in [1, 10]")
        Mockito.verify(randomMock, Mockito.times(1)).readDiscreteUniform(1, 10)
        // Test invalid input
        Mockito.`when`(randomMock.readDiscreteUniform(10, 1)).thenThrow(IllegalArgumentException("min must be less than or equal to max"))
        assertFailsWith<IllegalArgumentException> { randomMock.readDiscreteUniform(10, 1) }
    }

    @Test
    fun testReadNormal() {
        Mockito.`when`(randomMock.readGaussian(0.0, 1.0)).thenReturn(1.5)
        val result = randomMock.readGaussian(0.0, 1.0)
        assertEquals(1.5, result, "readNormal should return mocked value")
        assertTrue(result.isFinite(), "readNormal must be finite")
        Mockito.verify(randomMock, Mockito.times(1)).readGaussian(0.0, 1.0)
        // Test invalid input
        Mockito.`when`(randomMock.readGaussian(0.0, 0.0)).thenThrow(IllegalArgumentException("Standard deviation must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readGaussian(0.0, 0.0) }
    }

    @Test
    fun testReadExponential() {
        Mockito.`when`(randomMock.readExponential(1.0)).thenReturn(2.0)
        val result = randomMock.readExponential(1.0)
        assertEquals(2.0, result, "readExponential should return mocked value")
        assertTrue(result >= 0.0, "readExponential must be non-negative")
        Mockito.verify(randomMock, Mockito.times(1)).readExponential(1.0)
        // Test invalid input
        Mockito.`when`(randomMock.readExponential(0.0)).thenThrow(IllegalArgumentException("Lambda must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readExponential(0.0) }
    }

    @Test
    fun testReadBinomial() {
        Mockito.`when`(randomMock.readBinomial(10, 0.5)).thenReturn(5)
        val result = randomMock.readBinomial(10, 0.5)
        assertEquals(5, result, "readBinomial should return mocked value")
        assertContains(0..10, result, "readBinomial must be in [0, 10]")
        Mockito.verify(randomMock, Mockito.times(1)).readBinomial(10, 0.5)
        // Test invalid inputs
        Mockito.`when`(randomMock.readBinomial(-1, 0.5)).thenThrow(IllegalArgumentException("Number of trials must be non-negative"))
        Mockito.`when`(randomMock.readBinomial(10, -0.1)).thenThrow(IllegalArgumentException("Probability must be in [0, 1]"))
        Mockito.`when`(randomMock.readBinomial(10, 1.1)).thenThrow(IllegalArgumentException("Probability must be in [0, 1]"))
        assertFailsWith<IllegalArgumentException> { randomMock.readBinomial(-1, 0.5) }
        assertFailsWith<IllegalArgumentException> { randomMock.readBinomial(10, -0.1) }
        assertFailsWith<IllegalArgumentException> { randomMock.readBinomial(10, 1.1) }
    }

    @Test
    fun testReadPoisson() {
        Mockito.`when`(randomMock.readPoisson(4.0)).thenReturn(3)
        val result = randomMock.readPoisson(4.0)
        assertEquals(3, result, "readPoisson should return mocked value")
        assertTrue(result >= 0, "readPoisson must be non-negative")
        Mockito.verify(randomMock, Mockito.times(1)).readPoisson(4.0)
        // Test invalid input
        Mockito.`when`(randomMock.readPoisson(0.0)).thenThrow(IllegalArgumentException("Lambda must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readPoisson(0.0) }
    }

    @Test
    fun testReadGeometric() {
        Mockito.`when`(randomMock.readGeometric(0.5)).thenReturn(2)
        val result = randomMock.readGeometric(0.5)
        assertEquals(2, result, "readGeometric should return mocked value")
        assertTrue(result >= 1, "readGeometric must be at least 1")
        Mockito.verify(randomMock, Mockito.times(1)).readGeometric(0.5)
        // Test invalid inputs
        Mockito.`when`(randomMock.readGeometric(0.0)).thenThrow(IllegalArgumentException("Probability must be in (0, 1]"))
        Mockito.`when`(randomMock.readGeometric(1.1)).thenThrow(IllegalArgumentException("Probability must be in (0, 1]"))
        assertFailsWith<IllegalArgumentException> { randomMock.readGeometric(0.0) }
        assertFailsWith<IllegalArgumentException> { randomMock.readGeometric(1.1) }
    }

    @Test
    fun testReadNegativeBinomial() {
        Mockito.`when`(randomMock.readNegativeBinomial(5, 0.5)).thenReturn(4)
        val result = randomMock.readNegativeBinomial(5, 0.5)
        assertEquals(4, result, "readNegativeBinomial should return mocked value")
        assertTrue(result >= 0, "readNegativeBinomial must be non-negative")
        Mockito.verify(randomMock, Mockito.times(1)).readNegativeBinomial(5, 0.5)
        // Test invalid inputs
        Mockito.`when`(randomMock.readNegativeBinomial(0, 0.5)).thenThrow(IllegalArgumentException("Number of successes must be positive"))
        Mockito.`when`(randomMock.readNegativeBinomial(5, 0.0)).thenThrow(IllegalArgumentException("Probability must be in (0, 1]"))
        assertFailsWith<IllegalArgumentException> { randomMock.readNegativeBinomial(0, 0.5) }
        assertFailsWith<IllegalArgumentException> { randomMock.readNegativeBinomial(5, 0.0) }
    }

    @Test
    fun testReadGamma() {
        Mockito.`when`(randomMock.readGamma(2.0, 2.0)).thenReturn(3.5)
        val result = randomMock.readGamma(2.0, 2.0)
        assertEquals(3.5, result, "readGamma should return mocked value")
        assertTrue(result >= 0.0, "readGamma must be non-negative")
        Mockito.verify(randomMock, Mockito.times(1)).readGamma(2.0, 2.0)
        // Test invalid inputs
        Mockito.`when`(randomMock.readGamma(0.0, 2.0)).thenThrow(IllegalArgumentException("Shape must be positive"))
        Mockito.`when`(randomMock.readGamma(2.0, 0.0)).thenThrow(IllegalArgumentException("Scale must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readGamma(0.0, 2.0) }
        assertFailsWith<IllegalArgumentException> { randomMock.readGamma(2.0, 0.0) }
    }

    @Test
    fun testReadBeta() {
        Mockito.`when`(randomMock.readBeta(2.0, 5.0)).thenReturn(0.4)
        val result = randomMock.readBeta(2.0, 5.0)
        assertEquals(0.4, result, "readBeta should return mocked value")
        assertContains(0.0..1.0, result, "readBeta must be in [0.0, 1.0]")
        Mockito.verify(randomMock, Mockito.times(1)).readBeta(2.0, 5.0)
        // Test invalid inputs
        Mockito.`when`(randomMock.readBeta(0.0, 5.0)).thenThrow(IllegalArgumentException("Alpha must be positive"))
        Mockito.`when`(randomMock.readBeta(2.0, 0.0)).thenThrow(IllegalArgumentException("Beta must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readBeta(0.0, 5.0) }
        assertFailsWith<IllegalArgumentException> { randomMock.readBeta(2.0, 0.0) }
    }

    @Test
    fun testReadChiSquare() {
        Mockito.`when`(randomMock.readChiSquare(4)).thenReturn(3.2)
        val result = randomMock.readChiSquare(4)
        assertEquals(3.2, result, "readChiSquare should return mocked value")
        assertTrue(result >= 0.0, "readChiSquare must be non-negative")
        Mockito.verify(randomMock, Mockito.times(1)).readChiSquare(4)
        // Test invalid input
        Mockito.`when`(randomMock.readChiSquare(0)).thenThrow(IllegalArgumentException("Degrees of freedom must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readChiSquare(0) }
    }

    @Test
    fun testReadStudentT() {
        Mockito.`when`(randomMock.readStudentT(10)).thenReturn(1.8)
        val result = randomMock.readStudentT(10)
        assertEquals(1.8, result, "readStudentT should return mocked value")
        assertTrue(result.isFinite(), "readStudentT must be finite")
        Mockito.verify(randomMock, Mockito.times(1)).readStudentT(10)
        // Test invalid input
        Mockito.`when`(randomMock.readStudentT(0)).thenThrow(IllegalArgumentException("Degrees of freedom must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readStudentT(0) }
    }

    @Test
    fun testReadBernoulli() {
        Mockito.`when`(randomMock.readBernoulli(0.5)).thenReturn(1)
        val result = randomMock.readBernoulli(0.5)
        assertEquals(1, result, "readBernoulli should return mocked value")
        assertContains(0..1, result, "readBernoulli must be 0 or 1")
        Mockito.verify(randomMock, Mockito.times(1)).readBernoulli(0.5)
        // Test invalid inputs
        Mockito.`when`(randomMock.readBernoulli(-0.1)).thenThrow(IllegalArgumentException("Probability must be in [0, 1]"))
        Mockito.`when`(randomMock.readBernoulli(1.1)).thenThrow(IllegalArgumentException("Probability must be in [0, 1]"))
        assertFailsWith<IllegalArgumentException> { randomMock.readBernoulli(-0.1) }
        assertFailsWith<IllegalArgumentException> { randomMock.readBernoulli(1.1) }
    }

    @Test
    fun testReadLaplace() {
        Mockito.`when`(randomMock.readLaplace(0.0, 1.0)).thenReturn(-0.5)
        val result = randomMock.readLaplace(0.0, 1.0)
        assertEquals(-0.5, result, "readLaplace should return mocked value")
        assertTrue(result.isFinite(), "readLaplace must be finite")
        Mockito.verify(randomMock, Mockito.times(1)).readLaplace(0.0, 1.0)
        // Test invalid input
        Mockito.`when`(randomMock.readLaplace(0.0, 0.0)).thenThrow(IllegalArgumentException("Scale must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readLaplace(0.0, 0.0) }
    }

    @Test
    fun testReadLogistic() {
        Mockito.`when`(randomMock.readLogistic(0.0, 1.0)).thenReturn(0.7)
        val result = randomMock.readLogistic(0.0, 1.0)
        assertEquals(0.7, result, "readLogistic should return mocked value")
        assertTrue(result.isFinite(), "readLogistic must be finite")
        Mockito.verify(randomMock, Mockito.times(1)).readLogistic(0.0, 1.0)
        // Test invalid input
        Mockito.`when`(randomMock.readLogistic(0.0, 0.0)).thenThrow(IllegalArgumentException("Scale must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readLogistic(0.0, 0.0) }
    }

    @Test
    fun testReadCauchy() {
        Mockito.`when`(randomMock.readCauchy(0.0, 1.0)).thenReturn(2.0)
        val result = randomMock.readCauchy(0.0, 1.0)
        assertEquals(2.0, result, "readCauchy should return mocked value")
        assertTrue(result.isFinite() || result.isInfinite(), "readCauchy must be finite or infinite")
        Mockito.verify(randomMock, Mockito.times(1)).readCauchy(0.0, 1.0)
        // Test invalid input
        Mockito.`when`(randomMock.readCauchy(0.0, 0.0)).thenThrow(IllegalArgumentException("Gamma must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readCauchy(0.0, 0.0) }
    }

    @Test
    fun testReadF() {
        Mockito.`when`(randomMock.readFDist(5, 10)).thenReturn(1.2)
        val result = randomMock.readFDist(5, 10)
        assertEquals(1.2, result, "readF should return mocked value")
        assertTrue(result >= 0.0 || result.isInfinite(), "readF must be non-negative or infinite")
        Mockito.verify(randomMock, Mockito.times(1)).readFDist(5, 10)
        // Test invalid inputs
        Mockito.`when`(randomMock.readFDist(0, 10)).thenThrow(IllegalArgumentException("Numerator degrees of freedom must be positive"))
        Mockito.`when`(randomMock.readFDist(5, 0)).thenThrow(IllegalArgumentException("Denominator degrees of freedom must be positive"))
        assertFailsWith<IllegalArgumentException> { randomMock.readFDist(0, 10) }
        assertFailsWith<IllegalArgumentException> { randomMock.readFDist(5, 0) }
    }
}