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
 * Interface for importing a sequence of long values into some data object.
 *
 * Implementations define how to read long values from a given data structure using a provided lambda function.
 * This is useful for deserializing objects or reading long data in a customizable way.
 *
 * @param E The type of the data object to import longs into.
 */
public interface ImportOctetLong {
    /**
     * Imports a range of long values into the given data object.
     *
     * @param data The data object to import longs into.
     * @param offset The starting index in the data object.
     * @param length The number of long values to import.
     * @param readOctet Lambda function to read each long value, given its index.
     */
    public fun <E> importLongs(
        data: E,
        offset: Int,
        length: Int,
        readOctet: E.(index: Int) -> Long
    )
}