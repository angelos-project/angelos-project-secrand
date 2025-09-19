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
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize


public class SpongeChiObject(obj: Sponge): ChiObject<Sponge>(obj) {

    override val byteSize: Int
        get() = obj.byteSize

    override fun digest(data: ByteArray) {
        repeat(obj.visibleSize) {
            Octet.writeLE(
                obj.squeeze(it),
                data,
                it * TypeSize.longSize,
                TypeSize.longSize
            ) { index, value ->
                data[index] = value
            }
        }
        obj.round()
    }
}

/*public class EntropyAvalancheObject(obj: JitterEntropy): AvalancheObject<JitterEntropy>(obj) {
    public override val bufferSize: Int
        get() = 8

    public override val digestSize: Int
        get() = bufferSize * 8


    public override fun digest(data: LongArray) {
        obj.exportLongs(data, 0, data.size) { index, value ->
            data[index] = value
        }
    }
}

public class SecureFeedAvalancheObject(obj: SecureFeed): AvalancheObject<SecureFeed>(obj) {
    public override val bufferSize: Int
        get() = 8

    public override val digestSize: Int
        get() = bufferSize * 8


    public override fun digest(data: LongArray) {
        obj.exportLongs(data, 0, data.size) { index, value ->
            data[index] = value
        }
    }
}

public class SecureRandomAvalancheObject(obj: SecureRandom): AvalancheObject<SecureRandom>(obj) {
    public override val bufferSize: Int
        get() = 8

    public override val digestSize: Int
        get() = bufferSize * 8


    public override fun digest(data: LongArray) {
        repeat(bufferSize) {
            data[it] = obj.readLong()
        }
    }
}

public class GarbageGarblerAvalancheObject(obj: GarbageGarbler): AvalancheObject<GarbageGarbler>(obj) {
    public override val bufferSize: Int
        get() = 16

    public override val digestSize: Int
        get() = bufferSize * 8


    public override fun digest(data: LongArray) {
        repeat(bufferSize) {
            data[it] = obj.readLong()
        }
    }
}*/


public fun main() {
    val samples = 1024 * 32

    val benchmark0a = ChiSquare(samples) {
        SpongeChiObject(object : AbstractSponge21024(){})
    }
    benchmark0a.calculateData()
    println("AbstractSponge21024")
    println(benchmark0a)

    val benchmark0b = ChiSquare(samples) {
        SpongeChiObject(object : AbstractSponge1024(){})
    }
    benchmark0b.calculateData()
    println("AbstractSponge1024")
    println(benchmark0b)

    val benchmark1a = ChiSquare(samples) {
        SpongeChiObject(object : AbstractSponge2512(){})
    }
    benchmark1a.calculateData()
    println("AbstractSponge2512")
    println(benchmark1a)

    val benchmark1b = ChiSquare(samples) {
        SpongeChiObject(object : AbstractSponge512(){})
    }
    benchmark1b.calculateData()
    println("AbstractSponge512")
    println(benchmark1b)

    val benchmark2a = ChiSquare(samples) {
        SpongeChiObject(object : AbstractSponge2256(){})
    }
    benchmark2a.calculateData()
    println("AbstractSponge2256")
    println(benchmark2a)

    val benchmark2b = ChiSquare(samples) {
        SpongeChiObject(object : AbstractSponge256(){})
    }
    benchmark2b.calculateData()
    println("AbstractSponge256")
    println(benchmark2b)

    /*val benchmarkf = AvalancheEffect(samples) {
        SecureFeedAvalancheObject(SecureFeed)
    }
    benchmarkf.calculateData()
    println("SecureFeed")
    println(benchmarkf)

    val benchmark5 = AvalancheEffect(samples) {
        SecureRandomAvalancheObject(SecureRandom)
    }
    benchmark5.calculateData()
    println("SecureRandom")
    println(benchmark5)

    val benchmark6 = AvalancheEffect(samples) {
        GarbageGarblerAvalancheObject(GarbageGarbler())
    }
    benchmark6.calculateData()
    println("GarbageGarbler")
    println(benchmark6)

    val benchmark4 = AvalancheEffect(samples) {
        EntropyAvalancheObject(JitterEntropy)
    }
    benchmark4.calculateData()
    println("Entropy")
    println(benchmark4)*/
}