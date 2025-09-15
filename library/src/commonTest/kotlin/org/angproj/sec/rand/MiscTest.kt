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

    fun leIntBA(srcData: Int, toData: ByteArray): ByteArray {
        var fromData = srcData
        repeat(TypeSize.intSize) {
            toData[3-it] = (fromData and 0xff).toByte()
            fromData = fromData ushr 8
        }
        return toData
    }

    fun leIntBA2(src: Int, dst: ByteArray, offset: Int) {
        repeat(TypeSize.intSize) {
            dst[it + offset] = ((src ushr (3 - it) * 8) and 0xff).toByte()
        }
    }

    @Test
    fun testLittleEndianIntToByteArray() {
        val toData = ByteArray(TypeSize.intSize)
        leIntBA2(intData, toData, 0)
        assertContentEquals(toData, byteData)
    }

    fun baLeInt(srcData: ByteArray): Int {
        var toData = 0
        repeat(TypeSize.intSize) {
            toData = toData or (srcData[3-it].toInt() shl (8 * it))
        }
        return toData
    }

    fun baLeInt2(srcData: ByteArray, offset: Int): Int {
        var toData = 0
        repeat(TypeSize.intSize) {
            toData = toData or (srcData[offset + it].toInt() shl (8 * (3 - it)))
        }
        return toData
    }

    @Test
    fun testByteArrayToLittleEndianInt() {
        val toData = baLeInt2(byteData, 0)
        assertEquals(toData, intData)
    }
}