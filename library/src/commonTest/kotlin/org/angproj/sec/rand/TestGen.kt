package org.angproj.sec.rand

@OptIn(ExperimentalStdlibApi::class)
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
    }.toHexString(HexFormat.Default)

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

/**
 * Message
 * Hash result using RIPEMD-160
 * Hash result using RIPEMD-128
 * "" (empty string)	9c1185a5c5e9fc54612808977ee8f548b2258d31	cdf26213a150dc3ecb610f18f6b38b46
 * "a"	0bdc9d2d256b3ee9daae347be6f4dc835a467ffe	86be7afa339d0fc7cfc785e72f578d33
 * "abc"	8eb208f7e05d987a9b044a8e98c6b087f15a0bfc	c14a12199c66e4ba84636b0f69144c77
 * "message digest" 	5d0689ef49d2fae572b881b123a85ffa21595f36	9e327b3d6e523062afc1132d7df9d1b8
 * "a...z"1	f71c27109c692c1b56bbdceb5b9d2865b3708dbc	fd2aa607f71dc8f510714922b371834e
 * "abcdbcde...nopq"2	12a053384a9c0c88e405a06c27dcf49ada62eb2b	a1aa0689d0fafa2ddc22e88b49133a06
 * "A...Za...z0...9"3	b0e20b6e3116640286ed3a87a5713079b21f5189	d1e959eb179c911faea4624c60c5c702
 * 8 times "1234567890"	9b752e45573d4b39f4dbd3323cab82bf63326bfb	3f45ef194732c2dbb2c4a2c769795fa3
 * 1 million times "a"	52783243c1697bdbe16d37f97f68f08325dc1528	4a7f5723f954eba1216c9d8f6320431f
 *
 * */