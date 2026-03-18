/**
 * Copyright (c) 2024-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.sec

import org.angproj.sec.rand.AbstractSecurity
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.JitterEntropy

/**
 * SecureEntropy provides entropy using a 256-bit sponge seeded with jitter entropy.
 * It automatically reseeds on each export for high security.
 */
public object SecureEntropy : AbstractSecurity(object : AbstractSponge256() {}) {
    init {
        initialized = true
    }

    override fun reseedPolicy(bytesNeeded: Int): Boolean {
        seedEntropy(JitterEntropy)
        return true
    }
}