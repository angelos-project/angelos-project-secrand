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

import org.angproj.sec.stat.BitStatistic
import kotlin.math.absoluteValue

/**
 * Performs ceiling division of this integer by [chunkSize].
 *
 * Returns the smallest integer greater than or equal to the result of this value divided by [chunkSize].
 * If there is any remainder, the result is rounded up.
 *
 * Example: `7.ceilDiv(3)` returns `3`.
 *
 * @param chunkSize The divisor.
 * @return The ceiling of the division.
 * @throws ArithmeticException if [chunkSize] is zero.
 */
public fun Int.ceilDiv(other: Int): Int {
    val qout = this / other
    if ((this xor other) >= 0 && (qout * other != this)) return qout + 1
    return qout
}

/**
 * Performs ceiling division of this long value by [chunkSize].
 *
 * Returns the smallest long integer greater than or equal to the result of this value divided by [chunkSize].
 * If there is any remainder, the result is rounded up.
 *
 * Example: `7L.ceilDiv(3)` returns `3L`.
 *
 * @param chunkSize The divisor.
 * @return The ceiling of the division.
 * @throws ArithmeticException if [chunkSize] is zero.
 */
public fun Long.ceilDiv(other: Long): Long {
    val quot = this / other
    if ((this xor other) >= 0 && (quot * other != this)) return quot + 1
    return quot
}


/**
 * Computes the floor modulus of this integer with respect to [other].
 *
 * The floor modulus is always non-negative and is defined as:
 * `(a mod b + b) mod b`, ensuring the result is in the range `0 <= result < |b|`,
 * even if this value is negative.
 *
 * @param other The divisor for the modulus operation.
 * @return The non-negative floor modulus of this integer with respect to [other].
 * @throws ArithmeticException if [other] is zero.
 */
public fun Int.floorMod(other: Int): Int = this - this.floorDiv(other) * other


/**
 * Computes the floor modulus of this long value with respect to [other].
 *
 * The floor modulus is always non-negative and is defined as:
 * `(a mod b + b) mod b`, ensuring the result is in the range `0 <= result < |b|`,
 * even if this value is negative.
 *
 * @param other The divisor for the modulus operation.
 * @return The non-negative floor modulus of this long value with respect to [other].
 * @throws ArithmeticException if [other] is zero.
 */
public fun Long.floorMod(other: Long): Long = this - this.floorDiv(other) * other


/**
 * Unconditionally throws the specified exception from [exc] of type [T].
 *
 * This function is intended for scenarios where an exception needs to be thrown unconditionally.
 * The caller provides a lambda [exc] that constructs the exception to be thrown, allowing for custom exception types and messages.
 * By using a reified type parameter, the function enables flexible error signaling and supports a wide range of exception types.
 *
 * @param exc A lambda that returns an instance of the exception type [T] to be thrown.
 * @throws T The exception instance returned by [exc] is thrown unconditionally.
 */
public inline fun <reified T: Throwable> ensure(exc: () -> T): Nothing {
    throw exc()
}

/**
 * Evaluates the specified boolean condition [expr] and throws an exception of type [T] if the condition is false.
 *
 * This function is intended for enforcing preconditions, invariants, or postconditions in a generic and type-safe manner.
 * The caller provides a lambda [exc] that constructs the exception to be thrown, allowing for custom exception types and messages.
 * By using a reified type parameter, the function enables flexible error signaling and supports a wide range of exception types.
 *
 * @param expr The boolean condition to evaluate. If this condition is false, the exception provided by [exc] is thrown.
 * @param exc A lambda that returns an instance of the exception type [T] to be thrown if [expr] is false.
 * @throws T If [expr] evaluates to false, the exception instance returned by [exc] is thrown.
 */
public inline fun <reified T: Throwable> ensure(expr: Boolean, exc: () -> T) { if (!expr) ensure(exc) }


/**
 * Increments the Long value by the specified addition, wrapping around if it exceeds Long.MAX_VALUE.
 *
 * @param addition The value to add. Must be positive.
 * @return The incremented value, wrapped around if necessary.
 * @throws IllegalArgumentException if addition is not positive.
 */
public fun Long.incrementWithWrap(addition: Long): Long {
    require(addition > 0) { "Addition must be positive" }
    return when {
        this > Long.MAX_VALUE - addition -> (addition - (Long.MAX_VALUE - this) - 1) % Long.MAX_VALUE
        else -> this + addition
    }
}


/**
 * Increments the Long value by the specified addition (Int), wrapping around if it exceeds Long.MAX_VALUE.
 *
 * @param addition The value to add as Int. Must be positive.
 * @return The incremented value, wrapped around if necessary.
 * @throws IllegalArgumentException if addition is not positive.
 */
public fun Long.incrementWithWrap(addition: Int): Long = incrementWithWrap(addition.toLong())


/**
 * Converts this Int to a single-precision floating-point number.
 * The value is normalized to the range [0.0, 1.0) by masking the sign bit and dividing by 2^31.
 *
 * @return A single-precision floating-point number in the range [0.0, 1.0).
 */
public fun Int.toUnitFraction(): Float = ((this and 0x7FFFFFFF) / (1 shl 31).toFloat()).absoluteValue


/**
 * Converts this Long to a double-precision floating-point number.
 * The value is normalized to the range [0.0, 1.0) by masking the sign bit and dividing by 2^63.
 *
 * @return A double-precision floating-point number in the range [0.0, 1.0).
 */
public fun Long.toUnitFraction(): Double = ((this and 0x7fffffffffffffffL) / (1L shl 63).toDouble()).absoluteValue


/**
 * Collects statistical information about the bits in the given source data.
 *
 * This function analyzes the bits of the source data [src] using the provided [readOctet] function to read bytes.
 * It computes various statistics, including the total number of bits, counts of ones and zeros, distribution of hexadecimal values,
 * run lengths of consecutive bits, and counts of long runs. The results are encapsulated in a [BitStatistic] object.
 *
 * @param src The source data from which to collect bit statistics.
 * @param size The size of the source data in bytes. Must be greater than 0.
 * @param readOctet A function that reads a byte from the source data at a given index.
 * @return A [BitStatistic] object containing the collected statistics about the bits in the source data.
 * @throws IllegalArgumentException if [size] is not greater than 0.
 */
public fun<E> bitStatisticCollection(
    src: E,
    size: Int,
    readOctet: ReadOctet<E, Byte>
): BitStatistic {
    check(size > 0) { "Entropy cannot be empty" }

    var total = 0
    var longRun = 0
    val runs = IntArray(20)
    val hex = IntArray(16)
    var run = 0
    var ones = 0
    var zeros = 0
    var last = false
    var data = 0

    fun stat(bit: Boolean) {
        total++
        data = (data shl 1) or if (bit) 1 else 0
        if(total % 4 == 0) hex[data and 0xF]++
        if (bit) ones++ else zeros++
        if (bit == last) run++ else {
            if (run > 0) {
                if (run > 20) longRun++ else runs[run - 1]++
            }
            last = bit
            run = 1
        }
    }

    val iter = Octet.bitIterator(0 until size, src, readOctet)
    last = iter.next().also { stat(it) }

    iter.forEach { bit ->
        stat(bit)
    }

    if (run > 0) {
        if (run > 20) longRun++ else runs[run - 1]++
    }

    return BitStatistic(total, ones, zeros, hex.toList(), runs.toList(), longRun)
}