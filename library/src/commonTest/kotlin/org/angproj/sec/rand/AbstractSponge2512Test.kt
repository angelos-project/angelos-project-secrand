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

class AbstractSponge2512Test : SpongeHashTest<AbstractSponge2512>() {

    override val emptyHash = "bad18383571ac3cec7464ca257d3e6e27e5dc1e001cef26022d7f554d1a99654fa91b52ef782c5d9cc14c89cfad035bc4c36f71a09515d3a8bc750ce87e05558"
    override val singleAHash = "0c0a9160952f23f66508585c8702c8297e64abb64ee435b7c602258d80edfdf242ab4f0824dd7efe5780c5a508f4ce3cfec7447dd7e6196848abe346854d7c3b"
    override val abcHash = "48735d4796a158127058f04fd79587950974839b81ecd654053f65acdf18a9af9002133b8fc6f4852f4a8c3fb90a63021dd687b8948a37c9d2a509e3010cb98a"
    override val mdHash = "100806b9582414e41fb78f7e857544d262e4e992443778872b4ce634a28e128daf0519f86e2cce73f26270ce5d6876511fe5f0d716a59dac4bd1b17f30fe9acc"
    override val aToZHash = "9840fc6c0096e8cb38323f1f12ef36225700c88fa70fb8963370c67ff8b43192535c6426e68f08ed1c6fc2c3ea4e6b0ac3ff62454bf46200281336e2ea5886e2"
    override val nopqHash = "57af17ea6796588b817520ef09e927d22cc9308f8c4391914317118e3f7564a3724fce48f5435c9f3ae489c8fd6820ee9e9dca21c6e55926e9ebf555b144ddfc"
    override val alphaNumHash = "a215eb78c819531fbf498b6e841852a69e515a414b15a3db7339a8cd0a058cd75a3f89e2fecec50ae8b38209426e70660456d10a7488d6017368c9d76c5ddfd1"
    override val eightNumHash = "5ea9eb602d1a9116ce0ac4e7644b946b366e5eeef85c9a6a51bb7180213cebb22cd377347ccab8dec593d26c152812710973b1117a20fb1f7d9e2bf1b4cfeed8"
    override val millionAHash = "2fa25b40cbd8657889e81fce76b72453a3a36636fc97c3254c4808c2033d7f831583960ac9d4cc5531d91889a24651f146302dce8d28d78389a4204b37a2a251"

    class Hash2512 : Hash<AbstractSponge2512>(object : AbstractSponge2512() {}, debug)

    override fun getHashInstance(): Hash<AbstractSponge2512> {
        return Hash2512()
    }
}