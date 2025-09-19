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
 * Abstract base class for running benchmarks on a given object.
 *
 * @param B The type of the object being benchmarked.
 * @param E The type of the BenchmarkObject wrapper.
 * @property samples The number of samples to collect during benchmarking.
 * @property obj The benchmark object instance, created by the provided config function.
 *
 * Extend this class to implement specific benchmarking logic.
 * Use the config function to initialize the object to be benchmarked.
 */
public abstract class Benchmark<B, E: BenchmarkObject<B>>(
    public val samples: Int,
    config: () -> E
) {
    protected val obj: E = config()

    /**
     * Runs the benchmark and collects data.
     */
    public abstract fun calculateData()

    /**
     * Returns a string representation of the benchmark results.
     */
    public abstract override fun toString(): String
}