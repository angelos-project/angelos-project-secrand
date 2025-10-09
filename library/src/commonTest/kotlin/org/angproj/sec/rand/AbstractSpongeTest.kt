package org.angproj.sec.rand

import kotlin.test.Test
import kotlin.test.assertFailsWith


class MockSponge : AbstractSponge(4, 5) {
    override fun round() {
    }
}

open class AbstractSpongeTest {

    @Test
    fun testSpongeInit() {
        assertFailsWith<IllegalArgumentException> {
            MockSponge()
        }
    }
}