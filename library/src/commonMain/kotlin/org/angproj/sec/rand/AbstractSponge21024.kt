package org.angproj.sec.rand


/**
 * Custom cryptographic sponge with a linear 16-state array of 64-bit registers,
 * interpreted as a 4x4 matrix for operations.
 * Deterministic initialization, single-round permutation for absorb/squeeze, 16 rounds for scramble.
 */
public class AbstractSponge21024:  AbstractSponge(16, 16) {

    private inline fun<reified R: Any> diffuse(value: Long, up: Int, down: Int, right: Int, left: Int): Long {
        return value xor
                (sponge[up] ushr 3) xor // Rotate right by 3
                (sponge[down] shl 5) xor // Rotate left by 5
                (sponge[right] ushr 7) xor // Rotate right by 7
                (sponge[left] shl 11) // Rotate left by 11
    }

    private inline fun<reified R: Any> confuse(value: Long): Long {
        return value xor (-value.inv() * 11) xor (-value.inv() * 7)
    }

    /**
     * Single round of permutation, combining linear and non-linear transformations.
     * Treats the linear state as a 4x4 matrix for neighbor operations.
     */
    override fun round() {
        val temp = LongArray(spongeSize)
        // Step 1: Linear mixing (matrix-like diffusion)
        val sponge0 = diffuse<Unit>(sponge[0], 4, 12, 1, 3)
        val sponge1 = diffuse<Unit>(sponge[1], 5, 13, 2, 0)
        val sponge2 = diffuse<Unit>(sponge[2], 6, 14, 3, 1)
        val sponge3 = diffuse<Unit>(sponge[3], 7, 15, 0, 2)
        val sponge4 = diffuse<Unit>(sponge[4], 8, 0, 5, 7)
        val sponge5 = diffuse<Unit>(sponge[5], 9, 1, 6, 4)
        val sponge6 = diffuse<Unit>(sponge[6], 10, 2, 7, 5)
        val sponge7 = diffuse<Unit>(sponge[7], 11, 3, 4, 6)
        val sponge8 = diffuse<Unit>(sponge[8], 12, 4, 9, 11)
        val sponge9 = diffuse<Unit>(sponge[9], 13, 5, 10, 8)
        val sponge10 = diffuse<Unit>(sponge[10], 14, 6, 11, 9)
        val sponge11 = diffuse<Unit>(sponge[11], 15, 7, 8, 10)
        val sponge12 = diffuse<Unit>(sponge[12], 0, 8, 13, 15)
        val sponge13 = diffuse<Unit>(sponge[13], 1, 9, 14, 12)
        val sponge14 = diffuse<Unit>(sponge[14], 2, 10, 15, 13)
        val sponge15 = diffuse<Unit>(sponge[15], 3, 11, 12, 14)

        /*for (i in 0..15) {
            // Map linear index to 4x4 matrix coordinates
            val row = i / 4
            val col = i % 4
            // Map neighbor indices to linear array
            val up = ((row + 1) % 4) * 4 + col // (row + 1) % 4, col
            val down = ((row - 1 + 4) % 4) * 4 + col // (row - 1) % 4, col
            val right = row * 4 + ((col + 1) % 4) // row, (col + 1) % 4
            val left = row * 4 + ((col - 1 + 4) % 4) // row, (col - 1) % 4
            // Mix with neighbors (up, down, left, right, wrapping around)
            temp[i] = diffuse<Unit>(i, up, down, right, left)
        }*/

        mask = (mask and -counter.inv() and sponge1 and sponge2 and sponge3) xor
                ((sponge4 and sponge5 and sponge6 and sponge7) * 2) xor
                ((sponge8 and sponge9 and sponge10) * 4) xor
                ((sponge11 and sponge12) * 8) xor
                (sponge13 * 16)

        sponge[0] = confuse<Unit>(sponge0)
        sponge[1] = confuse<Unit>(sponge1)
        sponge[2] = confuse<Unit>(sponge2)
        sponge[3] = confuse<Unit>(sponge3)
        sponge[4] = confuse<Unit>(sponge4)
        sponge[5] = confuse<Unit>(sponge5)
        sponge[6] = confuse<Unit>(sponge6)
        sponge[7] = confuse<Unit>(sponge7)
        sponge[8] = confuse<Unit>(sponge8)
        sponge[9] = confuse<Unit>(sponge9)
        sponge[10] = confuse<Unit>(sponge10)
        sponge[11] = confuse<Unit>(sponge11)
        sponge[12] = confuse<Unit>(sponge12)
        sponge[13] = confuse<Unit>(sponge13)
        sponge[14] = confuse<Unit>(sponge14)
        sponge[15] = confuse<Unit>(sponge15)

        /*for(i in 0..15) {
            val x = temp[i]
            // Step 2: Non-linear transformation (S-box-like substitution)
            sponge[i] = x xor (-x.inv() * 11) xor (-x.inv() * 7)
            //state[i] = x xor (x shl 17) xor (x ushr 23)
        }*/

        // Step 3: Add round constant to prevent fixed points
        // Apply to the first register (equivalent to state[0][0] in 4x4 matrix)
        sponge[0] = sponge[0] xor (-0x5a5a5a5a5a5a5a5bL xor counter)
    }
}