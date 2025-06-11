package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.Entropy
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

public class EntropyAvalancheObject(obj: Entropy): AvalancheObject<Entropy>(obj) {
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
        EntropyAvalancheObject(Entropy)
    }
    benchmark4.calculateData()
    println("Entropy")
    println(benchmark4)
}