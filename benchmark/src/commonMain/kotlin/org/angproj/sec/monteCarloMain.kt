package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.Sponge
import kotlin.math.absoluteValue


public class SpongeMonteObject(obj: Sponge): MonteObject<Sponge>(obj) {
    public override fun readNextDouble(data: DoubleArray) {
        repeat(obj.visibleSize) {
            data[it] = ((obj.squeeze(it) and 0x7fffffffffffffffL) / (1L shl 63).toDouble()).absoluteValue
        }
        obj.round()
    }

    public override val bufferSize: Int
        get() = this.obj.visibleSize
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
}