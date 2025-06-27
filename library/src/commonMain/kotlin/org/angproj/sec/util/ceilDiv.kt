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