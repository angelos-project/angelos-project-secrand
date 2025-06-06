package org.anhproj.sec.rand

import kotlin.test.Test
import kotlin.test.assertContentEquals

class TestAbstractSponge512 {

    val empty = byteArrayOf(
        -84, -55, -78, 54, -92, 72, -31, 79, 20, -75, -112, 16, -75, -108, 39, -44, -26, 106,
        -103, -125, -123, -68, -7, -17, -14, 99, 66, -77, -80, -116, -105, 70, -108, 107, -83,
        55, -89, -48, 58, 71, -106, -44, -50, 88, -24, -2, -65, -24, 79, -85, -52, 33, -63,
        -90, -54, 33, -10, 77, -53, -107, -80, 20, 122, 88
    )

    @Test
    fun testSqueeze() {
        val hash = Hash512()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}