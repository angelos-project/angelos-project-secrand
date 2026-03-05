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


public fun hashDigestOf(hash: Hash, block: Hash.() -> Unit): ByteArray {
    hash.init()
    hash.block()
    return hash.final()
}

public fun hash256(): Hash = object : Hash(object : AbstractSponge256() {}) {}

public fun hash512(): Hash = object : Hash(object : AbstractSponge512() {}) {}

public fun hash1024(): Hash = object : Hash(object : AbstractSponge1024() {}) {}

public fun hash2256(): Hash = object : Hash(object : AbstractSponge2256() {}) {}

public fun hash2512(): Hash = object : Hash(object : AbstractSponge2512() {}) {}

public fun hash21024(): Hash = object : Hash(object : AbstractSponge21024() {}) {}
