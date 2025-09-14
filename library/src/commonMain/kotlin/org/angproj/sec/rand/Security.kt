package org.angproj.sec.rand

internal interface Security : Sponge, Randomizer {
    public var position: Int

    override fun getNextBits(bits: Int): Int {
        val random = squeeze(position++)

        if(position >= visibleSize) {
            round()
            position = 0
        }
        return Randomizer.reduceBits<Unit>(bits, Randomizer.foldBits<Unit>(random))
    }
}