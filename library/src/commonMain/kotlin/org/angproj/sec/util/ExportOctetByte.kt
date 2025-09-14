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

/**
 * Interface for exporting a sequence of bytes from some data object.
 *
 * Implementations define how to extract bytes from a given data structure and write them
 * using a provided lambda function. This is useful for serializing objects or transferring
 * byte data in a customizable way.
 *
 * @param E The type of the data object to export bytes from.
 */
public interface ExportOctetByte {
    /**
     * Exports a range of bytes from the given data object.
     *
     * @param data The data object to export bytes from.
     * @param offset The starting index in the data object.
     * @param length The number of bytes to export.
     * @param writeOctet Lambda function to write each byte, given its index and value.
     */
    public fun <E> exportBytes(
        data: E,
        offset: Int,
        length: Int,
        writeOctet: E.(index: Int, value: Byte) -> Unit
    )
}