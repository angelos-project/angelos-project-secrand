/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.sec

import org.angproj.sec.rand.*
import org.angproj.sec.util.toUnitFraction


public class SpongeMonteObject(obj: Sponge): MonteObject<Sponge>(obj) {
    public override fun readNextDouble(data: DoubleArray) {
        repeat(obj.visibleSize) {
            //data[it] = ((obj.squeeze(it) and 0x7fffffffffffffffL) / (1L shl 63).toDouble()).absoluteValue
            data[it] = obj.squeeze(it).toUnitFraction()

        }
        obj.round()
    }

    public override val bufferSize: Int
        get() = this.obj.visibleSize
}

public class EntropyMonteObject(obj: JitterEntropy): MonteObject<JitterEntropy>(obj) {
    public override fun readNextDouble(data: DoubleArray) {
        val arr = LongArray(data.size)
        obj.exportLongs(arr, 0, arr.size) { index, value ->
            arr[index] = value
        }
        repeat(bufferSize) {
            //data[it] = ((arr[it] and 0x7fffffffffffffffL) / (1L shl 63).toDouble()).absoluteValue
            data[it] = arr[it].toUnitFraction()
        }
    }

    public override val bufferSize: Int
        get() = 16
}

public class SecureFeedMonteObject(obj: SecureFeed): MonteObject<SecureFeed>(obj) {
    public override fun readNextDouble(data: DoubleArray) {
        val arr = LongArray(data.size)
        obj.exportLongs(arr, 0, arr.size) { index, value ->
            arr[index] = value
        }
        repeat(bufferSize) {
            //data[it] = ((arr[it] and 0x7fffffffffffffffL) / (1L shl 63).toDouble()).absoluteValue
            data[it] = arr[it].toUnitFraction()
        }
    }

    public override val bufferSize: Int
        get() = 16
}

public class SecureRandomMonteObject(obj: SecureRandom): MonteObject<SecureRandom>(obj) {
    public override fun readNextDouble(data: DoubleArray) {
        repeat(8) {
            data[it] = obj.readDouble()
        }
    }

    public override val bufferSize: Int
        get() = 8
}

public class GarbageGarblerMonteObject(obj: GarbageGarbler): MonteObject<GarbageGarbler>(obj) {
    public override fun readNextDouble(data: DoubleArray) {
        repeat(16) {
            data[it] = obj.readDouble()
        }
    }

    public override val bufferSize: Int
        get() = 16
}

public fun main() {
    val samples = 10_000_000

    val benchmark = MonteCarlo(samples) {
        SpongeMonteObject(object : AbstractSponge1024(){})
    }
    benchmark.calculateData()
    println("AbstractSponge1024")
    println(benchmark)

    val benchmark2 = MonteCarlo(samples) {
        SpongeMonteObject(object : AbstractSponge512(){})
    }
    benchmark2.calculateData()
    println("AbstractSponge512")
    println(benchmark2)

    val benchmark3 = MonteCarlo(samples) {
        SpongeMonteObject(object : AbstractSponge256(){})
    }
    benchmark3.calculateData()
    println("AbstractSponge256")
    println(benchmark3)

    val benchmarkf = MonteCarlo(samples) {
        SecureFeedMonteObject(SecureFeed)
    }
    benchmarkf.calculateData()
    println("SecureFeed")
    println(benchmarkf)

    val benchmark5 = MonteCarlo(samples) {
        SecureRandomMonteObject(SecureRandom)
    }
    benchmark5.calculateData()
    println("SecureRandom")
    println(benchmark5)

    val benchmark6 = MonteCarlo(samples) {
        GarbageGarblerMonteObject(GarbageGarbler())
    }
    benchmark6.calculateData()
    println("GarbageGarbler")
    println(benchmark6)

    val benchmark4 = MonteCarlo(samples) {
        EntropyMonteObject(JitterEntropy)
    }
    benchmark4.calculateData()
    println("Entropy")
    println(benchmark4)
}