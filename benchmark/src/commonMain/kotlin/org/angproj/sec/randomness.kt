package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.Entropy
import org.angproj.sec.rand.Sponge
import kotlin.time.measureTime

public interface Digest : Sponge {
    public fun digest(data: LongArray) {
        repeat(visibleSize) {
            data[it] = squeeze(it)
        }
    }
}

public class Random1024 : AbstractSponge1024(), Digest {
    init {
        Entropy.exportLongs(sponge, 0, visibleSize) { index, value ->
            absorb(value, index)
        }
        scramble()
    }
}

public class Random512 : AbstractSponge512(), Digest {
    init {
        scramble()
    }
}

public class Random256 : AbstractSponge256(), Digest {
    init {
        scramble()
    }
}

public fun randomness(sponge: Digest, samples: Int): IntArray {
    val oldDigest = LongArray(sponge.visibleSize)
    val digest = LongArray(sponge.visibleSize)
    val stats = IntArray(sponge.visibleSize * Long.SIZE_BITS)

    stats.fill(0)
    repeat(samples) {
        sponge.digest(digest)

        var total = 0
        repeat(sponge.visibleSize) { idx ->
            total += (digest[idx] xor oldDigest[idx]).countOneBits()
        }
        stats[total]++
        digest.copyInto(oldDigest)
        sponge.round()
    }

    return stats
}

public fun randomstats(random: Digest) {
    var stats: IntArray
    val time = measureTime {
        stats = randomness(random, 10_000_000)
    }

    /*repeat(stats.size) {
        if(stats[it] > 0) println("$it: ${stats[it]}")
    }*/

    val usage = stats.count { it == 0 }
    val sum = stats.sum()

    println("Samples: " + sum)
    println("Max: " + stats.max())
    println("Avg: " + sum / usage.toFloat())
    println("Spread: " + (1 - usage / (random.byteSize * 8).toFloat()))
    println("Time: " + time)
    println()
}