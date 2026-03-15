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
 * Type alias for a function that writes a value to an object at a given index.
 */
public typealias WriteOctet<E, T> = E.(index: Int, value: T) -> Unit

/**
 * Type alias for a function that reads a value from an object at a given index.
 */
public typealias ReadOctet<E, T> = E.(index: Int) -> T


/**
 * Utility object for handling octet (byte) operations, including reading and writing in different endianness.
 */
public object Octet {

    /**
     * Interface for exporting bytes to a destination.
     */
    public fun interface ExportBytes<E> {

        /**
         * Exports bytes to the destination using the write octet function.
         *
         * @param dst the destination object.
         * @param offset the offset in the destination.
         * @param length the number of bytes to export.
         * @param writeOctet the function to write bytes.
         */
        public fun export(
            dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Byte>
        )
    }

    /**
     * Interface for exporting longs to a destination.
     */
    public fun interface ExportLongs<E> {

        /**
         * Exports longs to the destination using the write octet function.
         *
         * @param dst the destination object.
         * @param offset the offset in the destination.
         * @param length the number of longs to export.
         * @param writeOctet the function to write longs.
         */
        public fun export(
            dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Long>
        )
    }

    /**
     * Interface for producers that can export longs and bytes.
     */
    public interface Producer {
        /**
         * Exports longs to the destination.
         *
         * @param dst the destination object.
         * @param offset the offset in the destination.
         * @param length the number of longs to export.
         * @param writeOctet the function to write longs.
         */
        public fun<E> exportLongs(dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Long>)

        /**
         * Exports bytes to the destination.
         *
         * @param dst the destination object.
         * @param offset the offset in the destination.
         * @param length the number of bytes to export.
         * @param writeOctet the function to write bytes.
         */
        public fun<E> exportBytes(dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Byte>)
    }

    /**
     * Interface for importing bytes from a source.
     */
    public fun interface ImportBytes<E> {

        /**
         * Imports bytes from the source using the read octet function.
         *
         * @param src the source object.
         * @param offset the offset in the source.
         * @param length the number of bytes to import.
         * @param readOctet the function to read bytes.
         */
        public fun import(
            src: E, offset: Int, length: Int, readOctet: ReadOctet<E, Byte>
        )
    }

    /**
     * Interface for importing longs from a source.
     */
    public fun interface ImportLongs<E> {

        /**
         * Imports longs from the source using the read octet function.
         *
         * @param src the source object.
         * @param offset the offset in the source.
         * @param length the number of longs to import.
         * @param readOctet the function to read longs.
         */
        public fun import(
            src: E, offset: Int, length: Int, readOctet: ReadOctet<E, Long>
        )
    }

    /**
     * Indicates if the system is little-endian.
     */
    public val isLittleEndian: Boolean

    /**
     * Indicates if the system is big-endian.
     */
    public val isBigEndian: Boolean

    init {
        val valueData = 0x1122334455667788L
        val streamData = ByteArray(8) { (0x11 * (it+1)).toByte() }

        isLittleEndian = readRev(streamData, 0, streamData.size) { index ->
            streamData[index]
        } == valueData

        isBigEndian = read(streamData, 0, streamData.size) { index ->
            streamData[index]
        } == valueData

        check(isLittleEndian != isBigEndian)
    }

    /**
     * Reads a long value from the source in reverse byte order.
     *
     * @param src the source object.
     * @param index the starting index.
     * @param size the number of bytes to read.
     * @param readOctet the read function.
     * @return the read long value.
     */
    public fun<E> readRev(
        src: E,
        index: Int,
        size: Int,
        readOctet: ReadOctet<E, Byte>
    ): Long {
        require(size in 0 .. TypeSize.longSize)
        var dst: Long = 0
        repeat(size) {
            dst = dst or ((src.readOctet(index + it).toLong() shl (8 * (size - 1 - it))) and (0xffL shl (8 * (size - 1 - it))))
        }
        return dst
    }

    /**
     * Writes a long value to the destination in reverse byte order.
     *
     * @param src the long value to write.
     * @param dst the destination object.
     * @param index the starting index.
     * @param size the number of bytes to write.
     * @param writeOctet the write function.
     */
    public fun<E> writeRev(
        src: Long,
        dst: E,
        index: Int,
        size: Int,
        writeOctet: WriteOctet<E, Byte>
    ) {
        require(size in 0 .. TypeSize.longSize)
        repeat(size) {
            dst.writeOctet(it + index, ((src ushr ((size - 1) - it) * 8) and 0xff).toByte())
        }
    }

    /**
     * Reads a long value from the source in normal byte order.
     *
     * @param src the source object.
     * @param index the starting index.
     * @param size the number of bytes to read.
     * @param readOctet the read function.
     * @return the read long value.
     */
    public fun<E> read(
        src: E,
        index: Int,
        size: Int,
        readOctet: ReadOctet<E, Byte>
    ): Long {
        require(size in 0 .. TypeSize.longSize)
        var dst: Long = 0
        repeat(size) {
            dst = dst or ((src.readOctet(index + it).toLong() shl (8 * it)) and (0xffL shl (8 * it)))
        }
        return dst
    }

    /**
     * Writes a long value to the destination in normal byte order.
     *
     * @param src the long value to write.
     * @param dst the destination object.
     * @param index the starting index.
     * @param size the number of bytes to write.
     * @param writeOctet the write function.
     */
    public fun<E> write(
        src: Long,
        dst: E,
        index: Int,
        size: Int,
        writeOctet: WriteOctet<E, Byte>
    ) {
        require(size in 0 .. TypeSize.longSize)
        repeat(size) {
            dst.writeOctet(it + index, ((src ushr it * 8) and 0xff).toByte())
        }
    }

    /**
     * Reads a long value in network byte order (big-endian).
     *
     * @param src the source object.
     * @param index the starting index.
     * @param size the number of bytes to read.
     * @param readOctet the read function.
     * @return the read long value.
     */
    public fun<E> readNet(
        src: E, index: Int, size: Int, readOctet: ReadOctet<E, Byte>
    ): Long = when {
        isLittleEndian -> readRev(src, index, size, readOctet)
        isBigEndian -> read(src, index, size, readOctet)
        else -> error("Impossible endian")
    }

    /**
     * Writes a long value in network byte order (big-endian).
     *
     * @param src the long value to write.
     * @param dst the destination object.
     * @param index the starting index.
     * @param size the number of bytes to write.
     * @param writeOctet the write function.
     */
    public fun<E> writeNet(
        src: Long, dst: E, index: Int, size: Int, writeOctet: WriteOctet<E, Byte>
    ): Unit = when {
        isLittleEndian -> writeRev(src, dst, index, size, writeOctet)
        isBigEndian -> write(src, dst, index, size, writeOctet)
        else -> error("Impossible endian")
    }

    
    /**
     * Converts a byte to its hexadecimal representation and writes it to the data.
     *
     * @param src the byte to convert.
     * @param data the destination object.
     * @param index the starting index.
     * @param writeOctet the write function.
     * @return the new index after writing.
     */
    public fun<E> toHex(src: Byte, data: E, index: Int, writeOctet: E.(index: Int, value: Byte) -> Unit): Int {
        data.writeOctet(index, toHexChar<Unit>((src.toInt() shr 4) and 0xf))
        data.writeOctet(index+1, toHexChar<Unit>(src.toInt() and 0xf))
        return index+2
    }

    private inline fun<reified R: Any> toHexChar(n: Int): Byte = when {
        n < 10 -> n + 0x30
        else -> n - 10 + 0x61
    }.toByte()

    /**
     * Converts the byte array to a string of hexadecimal symbols.
     *
     * @return the hexadecimal string representation.
     */
    public fun ByteArray.asHexSymbols(): String = buildString {
        this@asHexSymbols.forEach {
            toHex(it, this, -1) { _, value ->
                append(value.toInt().toChar())
            }
        }
    }

    /**
     * Creates an iterator over the bits of the specified byte range.
     *
     * @param range the range of bytes.
     * @param src the source object.
     * @param readOctet the read function.
     * @return an iterator of booleans representing bits.
     */
    public fun<E> bitIterator(range: IntRange, src: E, readOctet: ReadOctet<E, Byte>): Iterator<Boolean> = object : Iterator<Boolean> {
        private var current = 0
        private var pos = range.first * 8
        private val end = (range.last + 1) * 8 - 1
        override fun hasNext() = pos <= end
        override fun next(): Boolean {
            val bitIndex = 7 - (pos % 8)
            if(bitIndex == 7) current = src.readOctet(pos / 8).toInt() and 0xff
            val res = (current and (1 shl bitIndex)) != 0
            pos++
            return res
        }
    }
}
