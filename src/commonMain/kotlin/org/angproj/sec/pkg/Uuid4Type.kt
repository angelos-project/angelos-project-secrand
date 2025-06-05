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
package org.angproj.sec.pkg

import org.angproj.aux.io.BinaryReadable
import org.angproj.aux.io.BinaryWritable
import org.angproj.aux.io.Retrievable
import org.angproj.aux.io.Storable
import org.angproj.aux.pkg.*
import org.angproj.aux.pkg.Enpackageable.StorageIter
import org.angproj.aux.pkg.type.BlockType
import org.angproj.sec.uuid.Uuid4
import org.angproj.sec.uuid.uuid4Of
import org.angproj.sec.uuid.isNull
import kotlin.jvm.JvmInline

@JvmInline
public value class Uuid4Type(public val value: Uuid4) : Enfoldable {

    override fun foldSize(foldFormat: FoldFormat): Int = when (foldFormat) {
        FoldFormat.BLOCK -> atomicSize
        FoldFormat.STREAM -> atomicSize + Enfoldable.OVERHEAD_LENGTH
    }

    public override fun enfoldBlock(outData: Storable, offset: Int): Int {
        require(!value.isNull()) { "Null UUID" }
        return BlockType(value.asBinary()).enfoldToBlock(outData, offset)
    }

    public override fun enfoldStream(outStream: BinaryWritable): Int {
        require(!value.isNull()) { "Null UUID" }
        return BlockType(value.asBinary()).enfoldToStreamByConvention(outStream, conventionType)
    }

    public companion object : Unfoldable<Uuid4Type> {
        override val foldFormatSupport: List<FoldFormat> = listOf(FoldFormat.BLOCK, FoldFormat.STREAM)
        override val conventionType: Convention = Convention.UUID4
        override val atomicSize: Int = 16

        public fun unfoldFromBlock(inData: Retrievable, uuid4: Uuid4): Int = unfoldFromBlock(inData, 0, uuid4)

        public fun unfoldFromBlock(inData: Retrievable, offset: Int, uuid4: Uuid4): Int {
            require(!uuid4.isNull()) { "Null UUID" }
            return BlockType.unfoldFromBlock(inData, offset, uuid4.asBinary())
        }

        public override fun unfoldBlock(inData: Retrievable, offset: Int): Uuid4Type {
            return Uuid4Type(uuid4Of(BlockType.unfoldFromBlock(inData, offset, atomicSize).block))
        }

        public override fun unfoldStream(inStream: BinaryReadable): Uuid4Type {
            return Uuid4Type(uuid4Of(BlockType.unfoldFromStreamByConvention(inStream, conventionType).block))
        }
    }
}

public val Convention.Companion.UUID4: Convention by lazy { Convention(10051, -51) }
public fun BinaryWritable.saveUuid4(value: Uuid4): Int = Uuid4Type(value).enfoldStream(this)
public fun StorageIter.saveUuid4(value: Uuid4): Int = Uuid4Type(value).enfoldBlock(storage, index).also { index += it }
public fun BinaryReadable.loadUuid4(): Uuid4 = Uuid4Type.unfoldStream(this).value
public fun FoldFormat.sizeOf(value: Uuid4): Int = Uuid4Type(value).foldSize(this)
