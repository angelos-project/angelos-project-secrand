package org.angproj.sec.util

import kotlin.test.Test

class OctetTest {

    @Test
    fun testReverseOrder() {
        val testData = 0x1122334455667788L
        val array = ByteArray(8) { (0x11 * (it+1)).toByte() }
        println(Octet.asHexSymbolString(array))
        println(testData.toString(16))

        val bigEndian = ByteArray(8)
        Octet.writeBE(testData, bigEndian, 0, bigEndian.size) { index, value ->
            bigEndian[index] = value
        }
        println(Octet.asHexSymbolString(bigEndian))

        val bigLong = Octet.readBE(bigEndian, 0, bigEndian.size) { index ->
            bigEndian[index]
        }
        println(bigLong.toString(16))
    }

    @Test
    fun testLittleEndianOrder() {
        val testData = 0x1122334455667788L
        val array = ByteArray(8) { (0x11 * (it+1)).toByte() }
        println(Octet.asHexSymbolString(array))
        println(testData.toString(16))

        val littleEndian = ByteArray(8)
        Octet.writeLE(testData, littleEndian, 0, littleEndian.size) { index, value ->
            littleEndian[index] = value
        }
        println(Octet.asHexSymbolString(littleEndian))

        val littleLong = Octet.readLE(array, 0, array.size) { index ->
            array[index]
        }
        println(littleLong.toString(16))
    }
}