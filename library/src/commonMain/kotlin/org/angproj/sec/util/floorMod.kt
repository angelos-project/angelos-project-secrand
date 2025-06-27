/**
 * Copyright (c) 2023-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
