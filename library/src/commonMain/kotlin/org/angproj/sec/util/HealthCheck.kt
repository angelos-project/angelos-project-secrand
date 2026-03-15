/**
 * Copyright (c) 2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.hash.squeezerOf
import org.angproj.sec.rand.AbstractSecurity
import org.angproj.sec.rand.RandomBits
import org.angproj.sec.rand.Sponge
import org.angproj.sec.stat.BitStatisticCollector
import org.angproj.sec.stat.BitStatisticSnapshot
import org.angproj.sec.stat.securityHealthCheck
import org.angproj.sec.util.Octet.asHexSymbols

/**
 * Utility class for performing health checks on random data.
 * It analyzes bit statistics from various sources and provides methods to validate randomness.
 */
public class HealthCheck : BitStatisticCollector() {
    private fun consumeLong(value: Long): Unit = consume<Unit>(value, TypeSize.longBits)

    private fun consumeIterator(iter: Iterator<Boolean>) {
        val first = iter.next()
        setup(first)
        collectBit<Unit>(first)
        while (iter.hasNext()) { collectBit<Unit>(iter.next()) }
        finish()
    }

    /**
     * Analyzes an iterator of booleans and returns the bit statistics.
     *
     * @param iter the iterator of boolean values.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyze(iter: Iterator<Boolean>): BitStatisticSnapshot {
        consumeIterator(iter)
        return snapshot().also { reset() }
    }

    /**
     * Analyzes data from a source using a read octet function.
     *
     * @param src the source object.
     * @param size the size of the data.
     * @param readOctet the function to read bytes.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun<E> analyze(
        src: E, size: Int, readOctet: ReadOctet<E, Byte>
    ): BitStatisticSnapshot = analyze(Octet.bitIterator(0..<size, src, readOctet))

    /**
     * Analyzes a byte array.
     *
     * @param src the byte array to analyze.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyzeByteArray(src: ByteArray): BitStatisticSnapshot = analyze(src, src.size) { idx -> src[idx] }

    private inline fun<reified E: Any> setFirst(bitSize: Int, value: Long) {
        if(total == 0) setup(boolFromIndex<Unit>(0, boolMask<Unit>(bitSize), value))

    }

    private inline fun<reified E: Any> useValue(index: Int, value: Long, debug: ByteArray) {
        setFirst<Unit>(TypeSize.longBits, value)
        consumeLong(value)
        if(debug.isNotEmpty()) Octet.write(value, debug, index * 8, TypeSize.longSize) { idx, octet ->
            debug[idx] = octet
        }
    }

    private inline fun<reified E: Any> useAnalyze(debug: ByteArray, valueGenerator: (Int) -> Long): BitStatisticSnapshot {
        require(debug.isEmpty() || debug.size == 1024) { "Debug sample must be exactly 1024 bytes: ${debug.size}" }
        repeat(128) { index ->
            useValue<Unit>(index, valueGenerator(index), debug)
        }
        finish()
        return snapshot().also { reset() }
    }

    /**
     * Analyzes random bits from a RandomBits instance.
     *
     * @param randomBits the RandomBits to analyze.
     * @param debug optional debug byte array.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyzeBits(randomBits: RandomBits, debug: ByteArray = byteArrayOf()): BitStatisticSnapshot {
        return useAnalyze<Unit>(debug) { RandomBits.nextBitsToLong(randomBits) }
    }

    /**
     * Analyzes a sponge.
     *
     * @param sponge the sponge to analyze.
     * @param debug optional debug byte array.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyzeSponge(sponge: Sponge, debug: ByteArray = byteArrayOf()): BitStatisticSnapshot {
        val squeezer = sponge.squeezerOf()
        return useAnalyze<Unit>(debug) {
            squeezer()
        }
    }

    /**
     * Analyzes an iterator of longs.
     *
     * @param iter the iterator of longs.
     * @param debug optional debug byte array.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyzeIter(iter: Iterator<Long>, debug: ByteArray = byteArrayOf()): BitStatisticSnapshot {
        return useAnalyze<Unit>(debug) { if(iter.hasNext()) iter.next() else 0L }
    }

    /**
     * Analyzes an AbstractSecurity instance.
     *
     * @param security the security instance to analyze.
     * @param debug optional debug byte array.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyzeSecurity(security: AbstractSecurity, debug: ByteArray = byteArrayOf()): BitStatisticSnapshot {
        return analyzeLongs(security::exportLongs, debug)
    }

    /**
     * Analyzes longs from an export function.
     *
     * @param exportLongs the function to export longs.
     * @param debug optional debug byte array.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyzeLongs(exportLongs: Octet.ExportLongs<Long>, debug: ByteArray = byteArrayOf()): BitStatisticSnapshot {
        require(debug.isEmpty() || debug.size == 1024) { "Debug sample must be exactly 1024 bytes: ${debug.size}" }
        exportLongs.export(0L, 0, 128) { index, value ->
            useValue<Unit>(index, value, debug)
        }
        finish()
        return snapshot().also { reset() }
    }

    /**
     * Analyzes bytes from an export function.
     *
     * @param exportBytes the function to export bytes.
     * @param debug optional debug byte array.
     * @return the BitStatisticSnapshot of the analysis.
     */
    public fun analyzeBytes(exportBytes: Octet.ExportBytes<Byte>, debug: ByteArray = byteArrayOf()): BitStatisticSnapshot {
        require(debug.isEmpty() || debug.size == 1024) { "Debug sample must be exactly 1024 bytes: ${debug.size}" }
        val bitSize = TypeSize.byteBits
        exportBytes.export(0, 0, 1024) { index, value ->
            setFirst<Unit>(bitSize, value.toLong())
            consume<Unit>(value.toLong(), bitSize)
            if(debug.isNotEmpty()) debug[index] = value
        }
        finish()
        return snapshot().also { reset() }
    }

    public companion object {

        /**
         * Performs a health check using a test function.
         *
         * @param test the test function.
         * @return true if the health check passes.
         */
        public fun healthCheck(
            test: HealthCheck.() -> BitStatisticSnapshot
        ): Boolean = HealthCheck().test().securityHealthCheck()

        /**
         * Performs a health check with sample using a test function.
         *
         * @param test the test function with sample.
         * @return true if the health check passes.
         */
        public fun healthCheckWithSample(
            test: HealthCheck.(ByteArray) -> BitStatisticSnapshot
        ): Boolean = HealthCheck().test(byteArrayOf()).securityHealthCheck()

        /**
         * Performs a double health check with sample.
         *
         * @param test the test function with sample.
         * @return true if both checks pass.
         */
        public fun doubleHealthCheckWithSample(
            test: HealthCheck.(ByteArray) -> BitStatisticSnapshot
        ): Boolean = healthCheckWithSample(test) || healthCheckWithSample(test)

        /**
         * Performs a health check with failed sample debug.
         *
         * @param test the test function with sample.
         * @return true if the health check passes.
         */
        public fun healthCheckFailedSample(
            test: HealthCheck.(ByteArray) -> BitStatisticSnapshot
        ): Boolean = ByteArray(1024).let { sample ->
            HealthCheck().test(sample).securityHealthCheck().also {
                if(!it) println("Debug sample: ${sample.asHexSymbols()}")
            }
        }

        /**
         * Performs a double health check with debug.
         *
         * @param test the test function with sample.
         * @return true if the checks pass.
         */
        public fun doubleHealthCheckDebug(
            test: HealthCheck.(ByteArray) -> BitStatisticSnapshot
        ): Boolean = healthCheckWithSample(test) || healthCheckFailedSample(test)

        /**
         * Performs a single health check with debug on a sample.
         *
         * @param sample the sample to check.
         * @return true if the health check passes.
         */
        public fun singleHealthCheckDebug(
            sample: ByteArray
        ): Boolean = HealthCheck().analyzeByteArray(sample).securityHealthCheck().also {
            if(!it) println("Debug sample: ${sample.asHexSymbols()}")
        }
    }
}