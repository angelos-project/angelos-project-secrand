package org.angproj.sec.rand

class TestGen {

    enum class NamedTest {
        TEST_EMPTY,
        TEST_A,
        TEST_ABC,
        TEST_MD,
        TEST_A_TO_Z,
        TEST_NOPQ,
        TEST_ALPHA_NUM,
        TEST_EIGHT_NUM,
        TEST_MILLION_A
    }

    fun<E: Sponge> runTest(name: NamedTest, hash: Hash<E>): String = when(name) {
        NamedTest.TEST_EMPTY -> emptyTest(hash)
        NamedTest.TEST_A -> singleATest(hash)
        NamedTest.TEST_ABC -> abcTest(hash)
        NamedTest.TEST_MD -> mdTest(hash)
        NamedTest.TEST_A_TO_Z -> aToZTest(hash)
        NamedTest.TEST_NOPQ -> nopqTest(hash)
        NamedTest.TEST_ALPHA_NUM -> alphaNumTest(hash)
        NamedTest.TEST_EIGHT_NUM -> eightNumTest(hash)
        NamedTest.TEST_MILLION_A -> millionATest(hash)
    }.toHex()

    private fun<E: Sponge> emptyTest(hash: Hash<E>): ByteArray {
        hash.update(byteArrayOf())
        return hash.digest()
    }

    private fun<E: Sponge> singleATest(hash: Hash<E>): ByteArray {
        hash.update("a".encodeToByteArray())
        return hash.digest()
    }

    private fun<E: Sponge> abcTest(hash: Hash<E>): ByteArray {
        hash.update("abc".encodeToByteArray())
        return hash.digest()
    }

    private fun<E: Sponge> mdTest(hash: Hash<E>): ByteArray {
        hash.update("message digest".encodeToByteArray())
        return hash.digest()
    }

    private fun<E: Sponge> aToZTest(hash: Hash<E>): ByteArray {
        hash.update(aToZGenerator().encodeToByteArray())
        return hash.digest()
    }

    private fun<E: Sponge> nopqTest(hash: Hash<E>): ByteArray {
        hash.update(nopqGenerator().encodeToByteArray())
        return hash.digest()
    }

    private fun<E: Sponge> alphaNumTest(hash: Hash<E>): ByteArray {
        hash.update((aToZGenerator().uppercase() + aToZGenerator() + numGenerator()).encodeToByteArray())
        return hash.digest()
    }

    private fun<E: Sponge> eightNumTest(hash: Hash<E>): ByteArray {
        val num = numGenerator().encodeToByteArray()
        repeat(8) {
            hash.update(num)
        }
        return hash.digest()
    }

    private fun<E: Sponge> millionATest(hash: Hash<E>): ByteArray {
        val num = "a".repeat(100).encodeToByteArray()
        repeat(10_000) {
            hash.update(num)
        }
        return hash.digest()
    }

    public fun numGenerator(): String {
        val sb = StringBuilder()
        (48 .. 57).forEach { sb.append(it.toChar()) }
        return sb.toString()
    }

    public fun aToZGenerator(): String {
        val sb = StringBuilder()
        (97 .. 122).forEach { sb.append(it.toChar()) }
        return sb.toString()
    }

    public fun nopqGenerator(): String {
        val sb = StringBuilder()
        val alphabet = aToZGenerator()
        (0 .. (110 - 97)).forEach {
            sb.append(alphabet.substring(it, it + 4))
        }
        return sb.toString()
    }
}