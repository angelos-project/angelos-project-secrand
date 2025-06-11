package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.Sponge


public class SpongeAvalancheObject(obj: Sponge): AvalancheObject<Sponge>(obj) {
    public override val bufferSize: Int
        get() = this.obj.visibleSize

    public override val digestSize: Int
        get() = obj.visibleSize * 8


    public override fun digest(data: LongArray) {
        repeat(obj.visibleSize) {
            data[it] = obj.squeeze(it)
        }
        obj.round()
    }
}


public fun main() {
    val samples = 10_000_000

    val benchmark = AvalancheEffect(samples) {
        SpongeAvalancheObject(object : AbstractSponge1024(){})
    }
    benchmark.calculateData()
    println("AbstractSponge1024")
    println(benchmark)

    val benchmark2 = AvalancheEffect(samples) {
        SpongeAvalancheObject(object : AbstractSponge512(){})
    }
    benchmark2.calculateData()
    println("AbstractSponge512")
    println(benchmark2)

    val benchmark3 = AvalancheEffect(samples) {
        SpongeAvalancheObject(object : AbstractSponge256(){})
    }
    benchmark3.calculateData()
    println("AbstractSponge256")
    println(benchmark3)
}