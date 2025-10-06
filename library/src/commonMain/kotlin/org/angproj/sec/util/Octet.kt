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


public typealias WriteOctet<E, T> = E.(index: Int, value: T) -> Unit

public typealias ReadOctet<E, T> = E.(index: Int) -> T


public object Octet {

    /**
     * Interface for exporting a sequence of bytes from some data object.
     *
     * Implementations define how to extract bytes from a given data structure and write them
     * using a provided lambda function. This is useful for serializing objects or transferring
     * byte data in a customizable way.
     *
     * @param E The type of the data object to export bytes from.
     */
    public fun interface ExportBytes<E> {

        /**
         * Exports a range of bytes from the given data object.
         *
         * @param data The data object to export bytes from.
         * @param offset The starting index in the data object.
         * @param length The number of bytes to export.
         * @param writeOctet Lambda function to write each byte, given its index and value.
         */
        public fun export(
            dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Byte>
        )
    }

    /**
     * Interface for exporting a sequence of long values from some data object.
     *
     * Implementations define how to extract long values from a given data structure and write them
     * using a provided lambda function. This is useful for serializing objects or transferring
     * long data in a customizable way.
     *
     * @param E The type of the data object to export longs from.
     */
    public fun interface ExportLongs<E> {

        /**
         * Exports a range of long values from the given data object.
         *
         * @param data The data object to export longs from.
         * @param offset The starting index in the data object.
         * @param length The number of long values to export.
         * @param writeOctet Lambda function to write each long value, given its index and value.
         */
        public fun export(
            dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Long>
        )
    }

    /**
     * Interface for importing a sequence of bytes into some data object.
     *
     * Implementations define how to read bytes from a given data structure using a provided lambda function.
     * This is useful for deserializing objects or reading byte data in a customizable way.
     *
     * @param E The type of the data object to import bytes into.
     */
    public interface ImportBytes<E> {

        /**
         * Imports a range of bytes into the given data object.
         *
         * @param data The data object to import bytes into.
         * @param offset The starting index in the data object.
         * @param length The number of bytes to import.
         * @param readOctet Lambda function to read each byte, given its index.
         */
        public fun import(
            src: E, offset: Int, length: Int, readOctet: ReadOctet<E, Byte>
        )
    }

    /**
     * Interface for importing a sequence of long values into some data object.
     *
     * Implementations define how to read long values from a given data structure using a provided lambda function.
     * This is useful for deserializing objects or reading long data in a customizable way.
     *
     * @param E The type of the data object to import longs into.
     */
    public interface ImportLongs<E> {

        /**
         * Imports a range of long values into the given data object.
         *
         * @param data The data object to import longs into.
         * @param offset The starting index in the data object.
         * @param length The number of long values to import.
         * @param readOctet Lambda function to read each long value, given its index.
         */
        public fun import(
            src: E, offset: Int, length: Int, readOctet: ReadOctet<E, Long>
        )
    }

    public fun<E> readLE(
        src: E,
        index: Int,
        size: Int,
        readOctet: ReadOctet<E, Byte>
    ): Long {
        require(size in TypeSize.shortSize..TypeSize.longSize)
        var dst: Long = 0
        repeat(size) {
            dst = dst or (src.readOctet(index + it).toLong() shl (8 * ((size - 1) - it)))
        }
        return dst
    }

    public fun<E> writeLE(
        src: Long,
        dst: E,
        index: Int,
        size: Int,
        writeOctet: WriteOctet<E, Byte>
    ) {
        require(size in TypeSize.shortSize..TypeSize.longSize)
        repeat(size) {
            dst.writeOctet(it + index, ((src ushr ((size - 1) - it) * 8) and 0xff).toByte())
        }
    }


    public fun<E> toHex(src: Byte, data: E, index: Int, writeOctet: E.(index: Int, value: Byte) -> Unit): Int {
        data.writeOctet(index, toHexChar<Unit>((src.toInt() shr 4) and 0xf))
        data.writeOctet(index+1, toHexChar<Unit>(src.toInt() and 0xf))
        return index+2
    }

    private inline fun<reified R: Any> toHexChar(n: Int): Byte = when {
        n < 10 -> n + 0x30
        else -> n - 10 + 0x61
    }.toByte()

    public fun asHexSymbolString(data: ByteArray): String = buildString {
        data.forEach {
            toHex(it, data, -1) { _, value ->
                append(value.toInt().toChar())
            }
        }
    }

    public fun ByteArray.importBytes(exporter: ExportBytes<ByteArray>) {
        exporter.export(this, 0, this.size) { index, value ->
            this[index] = value
        }
    }

    public fun ByteArray.exportBytes(importer: ImportBytes<ByteArray>, readOctet: ReadOctet<ByteArray, Byte>) {
        importer.import(this, 0, this.size, readOctet)
    }
}
