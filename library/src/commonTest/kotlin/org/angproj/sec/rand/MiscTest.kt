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

    @Test
    fun testLittleEndianIntToByteArray() {
        var fromData = intData
        val toData = ByteArray(TypeSize.intSize)
        repeat(TypeSize.intSize) {
            toData[3-it] = (fromData and 0xff).toByte()
            fromData = fromData ushr 8
        }
        assertContentEquals(toData, byteData)
    }

    @Test
    fun testByteArrayToLittleEndianInt() {
        val fromData = byteData.copyOf()
        var toData = 0
        repeat(TypeSize.intSize) {
            toData = toData or (fromData[3-it].toInt() shl (8 * it))
        }
        assertEquals(toData, intData)
    }
}