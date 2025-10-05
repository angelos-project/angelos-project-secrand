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

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge21024
import org.angproj.sec.rand.AbstractSponge2256
import org.angproj.sec.rand.AbstractSponge2512
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.Sponge

public fun<R: Sponge> hashDigestOf(sponge: Hash<R>, block: Hash<R>.() -> Unit): ByteArray {
    sponge.init()
    sponge.block()
    return sponge.final()
}

public fun hash256(): Hash<AbstractSponge256> = object : Hash<AbstractSponge256>(object : AbstractSponge256() {}) {}

public fun hash512(): Hash<AbstractSponge512> = object : Hash<AbstractSponge512>(object : AbstractSponge512() {}) {}

public fun hash1024(): Hash<AbstractSponge1024> = object : Hash<AbstractSponge1024>(object : AbstractSponge1024() {}) {}

public fun hash2256(): Hash<AbstractSponge2256> = object : Hash<AbstractSponge2256>(object : AbstractSponge2256() {}) {}

public fun hash2512(): Hash<AbstractSponge2512> = object : Hash<AbstractSponge2512>(object : AbstractSponge2512() {}) {}

public fun hash21024(): Hash<AbstractSponge21024> = object : Hash<AbstractSponge21024>(object : AbstractSponge21024() {}) {}