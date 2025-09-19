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

import kotlin.math.absoluteValue


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