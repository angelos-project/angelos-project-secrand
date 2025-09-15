package org.angproj.sec.rand

import org.angproj.sec.util.TypeSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals


@OptIn(ExperimentalStdlibApi::class)
class MiscTest {

    val intData: Int = 0x11223344
    val byteData: ByteArray = byteArrayOf(0x11, 0x22, 0x33, 0x44)

    @Test
    fun testLittleEndianIntVsByteArray() {
        assertEquals(intData.toString(16), byteData.toHexString(HexFormat.Default))
    }

    fun writeLeLong2BeBinary(src: Long, dst: ByteArray, index: Int, size: Int) {
        repeat(size) {
            dst[it + index] = ((src ushr ((size - 1) - it) * 8) and 0xff).toByte()
        }
    }

    @Test
    fun testLittleEndianIntToByteArray() {
        val toData = ByteArray(TypeSize.intSize)
        writeLeLong2BeBinary(intData.toLong(), toData, 0, 4)
        assertContentEquals(toData, byteData)
    }

    fun readBeBinary2LeLong(src: ByteArray, index: Int, size: Int): Long {
        var dst: Long = 0
        repeat(size) {
            dst = dst or (src[index + it].toLong() shl (8 * ((size - 1) - it)))
        }
        return dst
    }

    @Test
    fun testByteArrayToLittleEndianInt() {
        val toData = readBeBinary2LeLong(byteData, 0, 4).toInt()
        assertEquals(toData, intData)
    }

    @Test
    fun testNumGenerator() {
        assertEquals(TestGen().numGenerator(), "0123456789")
    }

    @Test
    fun testAtoZ2Generator() {
        assertEquals(TestGen().aToZGenerator(), "abcdefghijklmnopqrstuvwxyz")
    }

    @Test
    fun testNopqGenerator() {
        assertEquals(TestGen().nopqGenerator(), "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq")
    }
}