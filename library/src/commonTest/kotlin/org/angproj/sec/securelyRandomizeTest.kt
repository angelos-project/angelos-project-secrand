package org.angproj.sec

import org.angproj.sec.util.securelyRandomize
import kotlin.test.Test

class securelyRandomizeTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testSecurelyRandomizeByteArray() {
        val array = ByteArray(53)
        array.securelyRandomize()
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testSecurelyRandomizeShortArray() {
        val array = ShortArray(36)
        array.securelyRandomize()
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testSecurelyRandomizeIntArray() {
        val array = IntArray(25)
        array.securelyRandomize()
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testSecurelyRandomizeLongArray() {
        val array = ShortArray(17)
        array.securelyRandomize()
    }
}