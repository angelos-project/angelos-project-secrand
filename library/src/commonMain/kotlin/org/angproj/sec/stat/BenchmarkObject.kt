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

/**
 * Abstract base class representing an object to be benchmarked.
 *
 * @param B The type of the object being benchmarked.
 * @property obj The instance of the object to benchmark.
 *
 * Extend this class to wrap the object you want to measure performance for.
 */
public abstract class BenchmarkObject<B>(protected val obj: B) {

    public abstract val sampleByteSize: Int

    public abstract fun nextSample(): ByteArray

    protected fun allocSampleArray(): ByteArray = ByteArray(sampleByteSize)
}