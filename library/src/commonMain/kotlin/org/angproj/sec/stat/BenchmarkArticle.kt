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
package org.angproj.sec.stat

import kotlin.math.max

/**
 * An abstract base class for benchmark articles that provide random samples for statistical testing.
 * It defines the interface for generating samples and calculating the sample byte size.
 *
 * @param B The type of the article object.
 * @property article The underlying article instance.
 */
public abstract class BenchmarkArticle<B>(protected val article: B) {

    /**
     * The byte size of each sample, ensuring a minimum of 16 bytes.
     */
    public val sampleByteSize: Int
        get() = max(16, byteSizeImpl())

    protected abstract fun byteSizeImpl(): Int

    /**
     * Generates the next random sample as a byte array.
     *
     * @return A byte array representing the next sample.
     */
    public abstract fun nextSample(): ByteArray

    protected fun allocSampleArray(): ByteArray = ByteArray(sampleByteSize)
}