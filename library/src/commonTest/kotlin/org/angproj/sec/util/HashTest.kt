package org.angproj.sec.util

import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.util.Octet.asHexSymbols
import kotlin.test.Test
import kotlin.test.assertEquals


class HashTest {

    private fun createHash(): Hash<AbstractSponge256> = object : Hash<AbstractSponge256>(
        object : AbstractSponge256() {}) {}

    @Test
    fun testUpdateAndFinalByteArray() {
        val hash = hash256()
        hash.init()

        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num)
        }

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            hash.final().asHexSymbols()
        )
    }

    @Test
    fun testUpdateAndFinalGeneric() {
        val hash = hash256()
        hash.init()

        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num)
        }

        val result = ByteArray(32)
        hash.final(result, 0, result.size) { index, value ->
            result[index] = value
        }

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            result.asHexSymbols()
        )
    }

    @Test
    fun testUpdateWithReadOctet() {
        val hash = hash256()
        hash.init()

        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num, 0, num.size) { index ->
                num[index]
            }
        }

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            hash.final().asHexSymbols()
        )
    }

    @Test
    fun testFinalStateTransition() {
        val hash = hash256()
        hash.init()

        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num, 0, num.size) { index ->
                num[index]
            }
        }

        val result = ByteArray(32)
        hash.final(result, 0, result.size) { index, value ->
            result[index] = value
        }

        assertEquals(
            "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4",
            result.asHexSymbols()
        )
    }
}