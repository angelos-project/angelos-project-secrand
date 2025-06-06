package org.anhproj.sec

import org.angproj.sec.SecureRandom
import kotlin.test.Test

class SecureRandomTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testReadBytes() {
        val bytes = ByteArray(1024)
        SecureRandom.readBytes(bytes)
        //println(bytes.toHexString())
    }
}