/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.aux.io.*
import org.angproj.aux.pipe.*
import kotlin.native.concurrent.ThreadLocal

/**
 * Portions a secure feed of random into a serviceable format of data for cryptographically secure use.
 * */
@ThreadLocal
public object SecureRandom : BinaryReadable, PumpReader, Reader {

    private val sink: BinarySink = buildSink { pull(SecureFeed).seg(DataSize._1K).buf(DataSize._1K).bin() }

    /*private val sink: BinarySink = PullPipe(
        Default,
        PumpSource(SecureFeed),
        DataSize._1K,
        DataSize._1K
    ).getBinSink()*/

    override val count: Long
        get() = sink.count

    private var _outputCnt: Long = 0
    override val outputCount: Long
        get() = _outputCnt

    override val outputStale: Boolean
        get() = !sink.isOpen()

    override fun readByte(): Byte = sink.readByte()
    override fun readUByte(): UByte = sink.readUByte()
    override fun readShort(): Short = sink.readShort()
    override fun readUShort(): UShort = sink.readUShort()
    override fun readInt(): Int = sink.readInt()
    override fun readUInt(): UInt = sink.readUInt()
    override fun readLong(): Long = sink.readLong()
    override fun readULong(): ULong = sink.readULong()
    override fun readFloat(): Float = sink.readFloat()
    override fun readDouble(): Double = sink.readDouble()

    override fun readRevShort(): Short = sink.readRevShort()
    override fun readRevUShort(): UShort = sink.readRevUShort()
    override fun readRevInt(): Int = sink.readRevInt()
    override fun readRevUInt(): UInt = sink.readRevUInt()
    override fun readRevLong(): Long = sink.readRevLong()
    override fun readRevULong(): ULong = sink.readRevULong()
    override fun readRevFloat(): Float = sink.readRevFloat()
    override fun readRevDouble(): Double = sink.readRevDouble()

    override fun read(data: Segment<*>): Int {
        var index = 0
        repeat(data.limit / TypeSize.long) {
            data.setLong(index, sink.readLong())
            index += TypeSize.long
        }
        repeat(data.limit % TypeSize.long) {
            data.setByte(index, sink.readByte())
            index++
        }
        _outputCnt += index
        return index
    }

    override fun read(bin: Binary): Int {
        var index = 0
        repeat(bin.limit / TypeSize.long) {
            bin.storeLong(index, sink.readLong())
            index += TypeSize.long
        }
        repeat(bin.limit % TypeSize.long) {
            bin.storeByte(index, sink.readByte())
            index++
        }
        _outputCnt += index
        return index
    }
}