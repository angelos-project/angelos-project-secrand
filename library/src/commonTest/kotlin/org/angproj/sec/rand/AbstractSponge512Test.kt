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
import org.angproj.sec.util.hash512

class AbstractSponge512Test : SpongeHashTest<AbstractSponge512>() {

    override val emptyHash = "4e5aeba74313e39d482e3c9497c939989a5b03e536b6dfd2e674aa6475ddb03e7457612476e489c60fe155e5d7a8d99fd5db9f2ffd49caff9ad1ee3503698e12"
    override val singleAHash = "aa443f6b0402137205c4ab5e3f906ff8777fded4889f2b5c79569c8a3735ea56bd18f23ba67b6ac1cab42ae295c6f1a8aa8f65d03257dc19564657c847d909da"
    override val abcHash = "e6ae7b24c214373ca4444e30ceda98422ecc01d51b492b9a6dba737dee9c0bb92694439cc7af31a89b3750440d3c79653790decaeaa29a0a9450b1fb2c6e0474"
    override val mdHash = "c3434b01176666e5858bf8ceae38f0eebd9a174151ea374ad6e6bd20c649af7ab65fff7477819564b995cd7c73ce2ab0855c6ef92c2ffc55a4fdca0fef750464"
    override val aToZHash = "f3edbfd3f482be221af19fb5e2e17dcd0d92ca83af12810dbac5bdf8ed0a1911e95ef7f9b96a1b4ca0b4ba972654fc3be891a060b9d34a34bbd127d6b6e0c9b6"
    override val nopqHash = "5d80440fd6495383373e1031798187885c57ec0f87513edd641b362c7372f73de5678eb223bd4690bb25d14bfcdcfa1694d1c0240d65b5dbff524e70942455b2"
    override val alphaNumHash = "b18f75a8731b496bdf6675a8ab6e557772357f1d591194968ffbb7e9dccb6ba675b9b3eb10c290eeff685688909ef95f22b64fa8284384cdf8d8d3a6d8b43f7c"
    override val eightNumHash = "60c8aa6437e98a09ee64acfa754eed4a01df1a37768aab26e58c4f10076f3e1a1abefc570505a0d21a69e0334ae5c2b896d6034d8d82d83f639928d56d2e4c97"
    override val millionAHash = "77b6e9ca8e14b896781db0c451c220db30f2aeeee4f4e6016900fad5157d9758065bfd19cf3469a09ba46bdf82c280c62cc3dbde2f261deb1b1c119105f2c591"

    override fun getHashInstance(): Hash<AbstractSponge512> {
        return hash512()
    }
}