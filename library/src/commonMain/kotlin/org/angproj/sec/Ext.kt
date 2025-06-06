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


/**
 * Reads bytes into a ByteArray from the secure random source.
 * This function fills the ByteArray with random bytes starting from a specified offset
 * and for a specified length. The data is written in little-endian order.
 *
 * @param data The ByteArray to fill with random bytes.
 * @param offset The starting index in the ByteArray to write to.
 * @param length The number of bytes to read. Defaults to the size of the ByteArray.
 */
public fun SecureFeed.read(data: ByteArray, offset: Int = 0, length: Int = data.size) {
    exportBytes(data, offset, length) { index, value ->
        this[index] = value
    }
}


/**
 * Reads random bytes into a ByteArray.
 *
 * @param data The ByteArray to fill with random bytes.
 * @param offset The starting index in the ByteArray to write to.
 * @param length The number of bytes to read. Defaults to the size of the ByteArray.
 */
public fun SecureEntropy.read(data: ByteArray, offset: Int = 0, length: Int = data.size) {
    exportBytes(data, offset, length) { index, value ->
        this[index] = value
    }
}


/**
 * Writes bytes from a ByteArray to the secure random source.
 * This function absorbs bytes starting from a specified offset and for a specified length.
 * The data is read in little-endian order.
 *
 * @param data The ByteArray to read bytes from.
 * @param offset The starting index in the ByteArray to read from.
 * @param length The number of bytes to write. Defaults to the size of the ByteArray.
 */
public fun GarbageGarbler.write(data: ByteArray, offset: Int = 0, length: Int = data.size) {
    importBytes(data, offset, length) { index ->
        this[index]
    }
}


/**
 * Reads bytes into a ByteArray from the secure random source.
 * This function fills the ByteArray with random bytes starting from a specified offset
 * and for a specified length. The data is written in little-endian order.
 *
 * @param data The ByteArray to fill with random bytes.
 * @param offset The starting index in the ByteArray to write to.
 * @param length The number of bytes to read. Defaults to the size of the ByteArray.
 */
public fun GarbageGarbler.read(data: ByteArray, offset: Int = 0, length: Int = data.size) {
    exportBytes(data, offset, length) { index, value ->
        this[index] = value
    }
}