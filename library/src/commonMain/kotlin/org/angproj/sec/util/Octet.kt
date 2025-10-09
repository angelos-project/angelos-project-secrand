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


/**
 * The `Octet` object provides utility functions and type aliases for working with octet (byte) data,
 * supporting both reading and writing operations in a generic and type-safe manner.
 *
 * It defines functional interfaces and type aliases to abstract the process of importing and exporting
 * bytes and long integers to and from various data structures. These abstractions allow for flexible
 * manipulation of binary data, such as serialization, deserialization, and conversion between different
 * representations.
 *
 * Key features include:
 * - Type aliases for read and write operations on octet data.
 * - Functional interfaces for exporting and importing bytes and long values.
 * - Utility functions for reading and writing little-endian values.
 * - Methods for converting byte data to hexadecimal string representations.
 * - Extension functions for integrating with `ByteArray` operations.
 *
 * The design enables interoperability and reusability across different platforms and data sources,
 * making it suitable for cryptographic, serialization, and low-level data manipulation tasks.
 */
public object Octet {

    /**
     * @param E The type of the destination to write bytes to.
     */
    public fun interface ExportBytes<E> {

        /**
         * Exports a range of bytes to the given destination [E].
         *
         * @param dst The destination object to write to.
         * @param offset The starting index at the destination.
         * @param length The number of bytes to write to the destination.
         * @param writeOctet Lambda function which writes the byte at the destination.
         */
        public fun export(
            dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Byte>
        )
    }

    /**
     * @param E The type of the destination to write long integers to.
     */
    public fun interface ExportLongs<E> {

        /**
         * Exports a range of bytes to the given destination [E].
         *
         * @param dst The destination object to write to.
         * @param offset The starting index at the destination.
         * @param length The number of long integers to write to the destination.
         * @param writeOctet Lambda function which writes the long integer at the destination.
         */
        public fun export(
            dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Long>
        )
    }

    /**
     * @param E The type of the source to read bytes from.
     */
    public fun interface ImportBytes<E> {

        /**
         * Imports a range of bytes from the given source.
         *
         * @param src The source object to read from.
         * @param offset The starting index at the source.
         * @param length The number of bytes to read from the source.
         * @param readOctet Lambda function which reads the bytes from the source.
         */
        public fun import(
            src: E, offset: Int, length: Int, readOctet: ReadOctet<E, Byte>
        )
    }

    /**
     * @param E The type of the source to read long integers from.
     */
    public fun interface ImportLongs<E> {

        /**
         * Imports a range of long integers from the given source.
         *
         * @param src The source object to read from.
         * @param offset The starting index at the source.
         * @param length The number of long integers to read from the source.
         * @param readOctet Lambda function which reads the long integers from the source.
         */
        public fun import(
            src: E, offset: Int, length: Int, readOctet: ReadOctet<E, Long>
        )
    }

    public val isLittleEndian: Boolean
    public val isBigEndian: Boolean

    init {
        val valueData = 0x1122334455667788L
        val streamData = ByteArray(8) { (0x11 * (it+1)).toByte() }

        isLittleEndian = readLE(streamData, 0, streamData.size) { index ->
            streamData[index]
        } == valueData

        isBigEndian = readBE(streamData, 0, streamData.size) { index ->
            streamData[index]
        } == valueData

        check(isLittleEndian != isBigEndian)
    }

    /**
     * Reading a long integer from a Big Endian stream [src] into a Little Endian architecture.
     * */
    public fun<E> readLE(
        src: E,
        index: Int,
        size: Int,
        readOctet: ReadOctet<E, Byte>
    ): Long {
        require(size in TypeSize.shortSize..TypeSize.longSize)
        var dst: Long = 0
        repeat(size) {
            dst = dst or ((src.readOctet(index + it).toLong() shl (8 * (size - 1 - it))) and (0xffL shl (8 * (size - 1 - it))))
        }
        return dst
    }

    /**
     * Writing a long integer [src] as bytes from a Little Endian architecture to a Big Endian stream [dst].
     * */
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

    /**
     * Reading a long integer from a Big Endian stream [src] into a Big Endian architecture.
     * */
    public fun<E> readBE(
        src: E,
        index: Int,
        size: Int,
        readOctet: ReadOctet<E, Byte>
    ): Long {
        require(size in TypeSize.shortSize..TypeSize.longSize)
        var dst: Long = 0
        repeat(size) {
            dst = dst or ((src.readOctet(index + it).toLong() shl (8 * it)) and (0xffL shl (8 * it)))
        }
        return dst
    }

    /**
     * Writing a long integer [src] as bytes from a Big Endian architecture to a Big Endian stream [dst].
     * */
    public fun<E> writeBE(
        src: Long,
        dst: E,
        index: Int,
        size: Int,
        writeOctet: WriteOctet<E, Byte>
    ) {
        require(size in TypeSize.shortSize..TypeSize.longSize)
        repeat(size) {
            dst.writeOctet(it + index, ((src ushr it * 8) and 0xff).toByte())
        }
    }

    /**
     * Reading a long integer from a Big Endian stream [src] into whichever endian architecture.
     * */
    public fun<E> readNet(
        src: E, index: Int, size: Int, readOctet: ReadOctet<E, Byte>
    ): Long = when {
        isLittleEndian -> readLE(src, index, size, readOctet)
        isBigEndian -> readBE(src, index, size, readOctet)
        else -> error("Impossible endian")
    }

    /**
     * Writing a long integer [src] as bytes from whichever endian architecture to a Big Endian stream [dst].
     * */
    public fun<E> writeNet(
        src: Long, dst: E, index: Int, size: Int, writeOctet: WriteOctet<E, Byte>
    ): Unit = when {
        isLittleEndian -> writeLE(src, dst, index, size, writeOctet)
        isBigEndian -> writeBE(src, dst, index, size, writeOctet)
        else -> error("Impossible endian")
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

    public fun ByteArray.asHexSymbols(): String = buildString {
        this@asHexSymbols.forEach {
            toHex(it, this, -1) { _, value ->
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
