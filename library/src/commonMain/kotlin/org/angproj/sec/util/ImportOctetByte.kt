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
 * Interface for importing a sequence of bytes into some data object.
 *
 * Implementations define how to read bytes from a given data structure using a provided lambda function.
 * This is useful for deserializing objects or reading byte data in a customizable way.
 *
 * @param E The type of the data object to import bytes into.
 */
public interface ImportOctetByte {
    /**
     * Imports a range of bytes into the given data object.
     *
     * @param data The data object to import bytes into.
     * @param offset The starting index in the data object.
     * @param length The number of bytes to import.
     * @param readOctet Lambda function to read each byte, given its index.
     */
    public fun <E> importBytes(
        data: E,
        offset: Int,
        length: Int,
        readOctet: E.(index: Int) -> Byte
    )
}