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
import kotlin.test.assertEquals

import java.nio.ByteBuffer
import java.nio.ByteOrder


public object FuzzerOctetWriteKt : FuzzPrefs() {

    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val value = data.consumeLong()
        val array = ByteArray(8)

        Octet.write(value, array, 0, array.size) { index, v ->
            array[index] = v
        }

        val buffer = ByteBuffer.wrap(array)
        if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            buffer.order(ByteOrder.LITTLE_ENDIAN)
        }
        val loaded = buffer.getLong()

        assertEquals(value, loaded)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${maxTotalTime}"
        ))
    }
}