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
package org.angproj.sec.rand

import org.angproj.sec.util.Hash

class AbstractSponge2256Test : SpongeHashTest<AbstractSponge2256>() {

    override val emptyHash = "3b3070f6518d906d2b09e2e9f5cb3798b1fe054e21ca16295108527653de8b3c"
    override val singleAHash = "1df634283445abdae323064c286e1d59e10d65f68627d50063261e37dfd8f6b1"
    override val abcHash = "31897117a758bbda163fb4d98072d579000f20b953aa2700be1f2291577bb6b1"
    override val mdHash = "3f19aa4337a831135cad087b18481933bc854f6136193bf7fc2c8855437de148"
    override val aToZHash = "70c29b6003954d7616a44255c03a6e42758ba6f3540c3f76610967fa57a6743f"
    override val nopqHash = "9442c0114ed378eaa82b9552a9c3ae76120c5f063380864d91306b306571be4e"
    override val alphaNumHash = "9e571b4dcf62b6afebe222f5cb4b3fdc5e213a8a2f8edd2e608b39011e4f0461"
    override val eightNumHash = "fb95718edf6720aba7b36c3ed4322045c2dd7fa7228ce1435b05518dfd86ad6c"
    override val millionAHash = "59962e5d65b9f81168c82e20147d5f502e97f3984971ef06b4dabb2b6f5127e0"

    class Hash2256 : Hash<AbstractSponge2256>(object : AbstractSponge2256() {})

    override fun getHashInstance(): Hash<AbstractSponge2256> {
        return Hash2256()
    }
}