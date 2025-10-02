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

import kotlin.time.Duration

/**
 * Data class representing the results of a statistical test.
 *
 * @property sampleCount The number of samples taken during the test.
 * @property keyValue The calculated key value from the test (e.g., estimated value of Pi).
 * @property duration The duration taken to complete the test.
 * @property report A textual report summarizing the results of the test.
 */
public data class Statistical(
    val sampleCount: Long,
    val keyValue: Double,
    val duration: Duration,
    val report: String
)