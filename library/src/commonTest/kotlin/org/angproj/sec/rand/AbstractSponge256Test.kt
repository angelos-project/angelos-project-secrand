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
import org.angproj.sec.util.hash256

class AbstractSponge256Test : SpongeTest<AbstractSponge256>() {

    override val emptyHash = "16ac5a3c169bbc6528f1763c28a7b6dccf9a5568cfbcd6caf656a5cff6e4b80e"
    override val singleAHash = "3fdd987dde638ac03fbd22a15c4663157b499b1fa6b8cf8f7717aea8a5cea6cd"
    override val abcHash = "b0888953fd976ac015b8e6ca25ab1315e042013b04796f8fe465401b4109a6cd"
    override val mdHash = "be01bdb3219cdedcabb94b284d4ed901f8d6d02d803c90f5ba78cdf4ba0c97d7"
    override val aToZHash = "fe4fbcadfb8ef3626b87b03b52526821ab1701138c710a31ed0bde84bd0f8e07"
    override val nopqHash = "e0f0677c2ff326ae1776a19ae7a8407ab8c10a40fd7821d77453ddd28307789b"
    override val alphaNumHash = "6c43b59c883cb799b290299808eed96d980f1473d6da499217cc1767c442536b"
    override val eightNumHash = "6c6157927708bcd67a21ea40037dc12f67cf6446902e81cffe1377790a1cb812"
    override val millionAHash = "6ecd3b8f83fe1848e1d8feda9e9d22542c24f7bda52d310853ca12a93fa0f3f4"

    override fun getHashInstance(): Hash<AbstractSponge256> {
        return hash256()
    }
}