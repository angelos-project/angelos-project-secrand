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


public abstract class AbstractDataImportExport {

    public fun <D, U: Number> import(data: D, size: Int, readOctet: D.(offset: Int) -> U): D {
        require(size > 0) { "Zero length data" }
        return data.apply {
            for (i in 0 until size) {
                readOctet(i)
            }
        }
    }

    public fun <D, U: Number> export(data: D, size: Int, writeOctet: D.(value: U) -> Unit): D {
        val source = longArrayOf(0, 1, 2, 3, 4, 5, 6, 7)

        repeat(size) { i ->
            writeOctet(i) // Initialize with zero or any default value
        }
    }

    private fun serializeLong(data: Long): ByteArray {
        repeat(8) { i ->
            val byte = (data shr (i * 8)).toByte()
            writeOctet(i, byte)
        }

    }
}