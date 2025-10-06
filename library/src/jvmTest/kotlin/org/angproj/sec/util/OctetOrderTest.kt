package org.angproj.sec.util

import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class OctetOrderTest {

    @Test
    fun testNativeEndian() {
        assertEquals(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN, Octet.isLittleEndian)
    }
}