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

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import org.angproj.sec.rand.AbstractSponge1024
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue


public object FuzzerSponge1024Kt : FuzzPrefs() {

    public class Sponge1024 : AbstractSponge1024(), SpongeImpl {}


    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val sponge = Sponge1024()

        var buffer1 = ByteArray(64)
        var buffer2 = ByteArray(64)

        try {
            sponge.reseed(data.consumeBytes(128))
            buffer1 = sponge.digest()
            sponge.round()
            buffer2 = sponge.digest()
        } catch (_: Exception) {
            assertTrue(false)
        }

        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${maxTotalTime}"
        ))
    }
}