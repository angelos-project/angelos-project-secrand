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

