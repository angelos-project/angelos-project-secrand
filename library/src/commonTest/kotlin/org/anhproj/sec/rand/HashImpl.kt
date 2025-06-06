package org.anhproj.sec.rand

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512


interface Hash {
    fun digest(): ByteArray
}


class Hash256 : AbstractSponge256(), Hash {
    init {
        scramble()
    }

    override fun digest(): ByteArray {
        val digestBytes = ByteArray(visibleSize * 8)
        var pos = 0
        repeat(visibleSize) {
            var state = squeeze(it)
            repeat(8) {
                digestBytes[pos++] = (state and 0xff).toByte()
                state = (state ushr 8)
            }
        }
        return digestBytes
    }
}


class Hash512 : AbstractSponge512(), Hash {
    init {
        scramble()
    }

    override fun digest(): ByteArray {
        val digestBytes = ByteArray(visibleSize * 8)
        var pos = 0
        repeat(visibleSize) {
            var state = squeeze(it)
            repeat(8) {
                digestBytes[pos++] = (state and 0xff).toByte()
                state = (state ushr 8)
            }
        }
        return digestBytes
    }
}


class Hash1024 : AbstractSponge1024(), Hash {
    init {
        scramble()
    }

    override fun digest(): ByteArray {
        val digestBytes = ByteArray(visibleSize * 8)
        var pos = 0
        repeat(visibleSize) {
            var state = squeeze(it)
            repeat(8) {
                digestBytes[pos++] = (state and 0xff).toByte()
                state = (state ushr 8)
            }
        }
        return digestBytes
    }
}