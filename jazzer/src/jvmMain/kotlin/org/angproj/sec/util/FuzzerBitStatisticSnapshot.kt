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
package org.angproj.sec.util

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import org.angproj.sec.FuzzPrefs
import org.angproj.sec.stat.bitStatisticOf
import kotlin.test.assertEquals


public object FuzzerBitStatisticSnapshotKt : FuzzPrefs() {

    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val array = data.consumeBytes(4096)

        assertEquals(bitStatisticOf(array), HealthCheck().analyze(array))
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${maxTotalTime}"
        ))
    }
}