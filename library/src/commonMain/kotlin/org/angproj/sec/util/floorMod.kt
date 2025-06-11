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

import kotlin.math.absoluteValue

/**
 * Returns the floor modulus of this integer with respect to another integer.
 * The result is always non-negative, even if the original number is negative.
 *
 * @param other The divisor for the modulus operation.
 * @return The non-negative floor modulus of this integer with respect to the other integer.
 */
public fun Int.floorMod(other: Int): Int = this.absoluteValue.mod(other.absoluteValue)

public fun Long.floorMod(other: Long): Long = this.absoluteValue.mod(other.absoluteValue)
