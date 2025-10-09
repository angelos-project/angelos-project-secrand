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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFailsWith

class UtilityTest {

    @Test
    fun testFloorMod() {
        assertEquals(55306002, (-1597657065).floorMod(236137581))
        assertEquals(1175710701, (-846147156).floorMod(2021857857))
        assertEquals(-18153568, 368851535.floorMod(-43000567))
        assertEquals(463564969, 2093931569.floorMod(815183300))
        assertEquals(433803757, (-1988291243).floorMod(807365000))
        assertEquals(-234080233, 1912707644.floorMod(-2146787877))
        assertEquals(-487588117, 1161191531.floorMod(-1648779648))
        assertEquals(-650384756, (-1649722584).floorMod(-999337828))
        assertEquals(77994495, 1729397415.floorMod(412850730))
        assertEquals(-268931827, (-653024851).floorMod(-384093024))
    }

    @Test
    fun testFloorModLong() {
        assertEquals(-8488878051231838307, (-8488878051231838307).floorMod(-8640794721079403460))
        assertEquals(670825087974070968, 2785726074212837120.floorMod(1057450493119383076))
        assertEquals(3481036217844994489, 7615168037683131341.floorMod(4134131819838136852))
        assertEquals(-632354115504421495, 3922506846446668987.floorMod(-2277430480975545241))
        assertEquals(2641759430158558008, (-8139569169272833968).floorMod(5390664299715695988))
        assertEquals(6221542812823928994, (-1962441781494791253).floorMod(8183984594318720247))
        assertEquals(89516388874986247, (-8551130637058547337).floorMod(8640647025933533584))
        assertEquals(75511830486119089, 4804435096524466289.floorMod(236446163301917360))
        assertEquals(-1985608619788849170, 3671404538260235211.floorMod(-5657013158049084381))
        assertEquals(-4023281906054237498, 3819327888542698247.floorMod(-7842609794596935745))
    }

    @Test
    fun testCeilDiv() {
        assertEquals(-44421, 1154953511.ceilDiv(-26000))
        assertEquals(-64794, 1226102786.ceilDiv(-18923))
        assertEquals(37456, 469131818.ceilDiv(12525))
        assertEquals(64701, (-877472867).ceilDiv(-13562))
        assertEquals(-112308, 1786597976.ceilDiv(-15908))
        assertEquals(18098, 455515670.ceilDiv(25170))
        assertEquals(136364, 660133416.ceilDiv(4841))
        assertEquals(-60992, 165960815.ceilDiv(-2721))
        assertEquals(-64292, 647872492.ceilDiv(-10077))
        assertEquals(-61655, (-1134776746).ceilDiv(18405))
    }

    @Test
    fun testCeilDivLong() {
        assertEquals(7972631363242157, (-7119559807375245629).ceilDiv(-893))
        assertEquals(269260437254900, 3370602153556830059.ceilDiv(12518))
        assertEquals(323495744506130, (-871497535699512424).ceilDiv(-2694))
        assertEquals(107164862689382, (-3057520697390751865).ceilDiv(-28531))
        assertEquals(-334761815029102, (-3872859438071691833).ceilDiv(11569))
        assertEquals(-199765707050660, (-5288597328459183895).ceilDiv(26474))
        assertEquals(-387849090299318, 5864278245325700844.ceilDiv(-15120))
        assertEquals(167352437997398, (-4306814991863030966).ceilDiv(-25735))
        assertEquals(-765824482401112, (-6445944668370165747).ceilDiv(8417))
        assertEquals(112844128399492, 1601709558502386393.ceilDiv(14194))
    }

    @Test
    fun testSecurelyRandomizeByteArray() {
        val size = 128
        val buffer1 = ByteArray(size)
        val buffer2 = ByteArray(size)

        buffer1.securelyRandomize()
        buffer2.securelyRandomize()

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testSecurelyRandomizeShortArray() {
        val size = 64
        val buffer1 = ShortArray(size)
        val buffer2 = ShortArray(size)

        buffer1.securelyRandomize()
        buffer2.securelyRandomize()

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testSecurelyRandomizeIntArray() {
        val size = 32
        val buffer1 = IntArray(size)
        val buffer2 = IntArray(size)

        buffer1.securelyRandomize()
        buffer2.securelyRandomize()

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testSecurelyRandomizeLongArray() {
        val size = 16
        val buffer1 = LongArray(size)
        val buffer2 = LongArray(size)

        buffer1.securelyRandomize()
        buffer2.securelyRandomize()

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testEnsureException() {
        // This should pass without throwing an exception
        ensure(1 < 2) { RuntimeException("This should not fail") }
        assertFailsWith<RuntimeException> {
            ensure(2 < 1) { RuntimeException("This should fail") }
        }
    }

    @Test
    fun testEnsureMandatory() {
        assertFailsWith<RuntimeException> {
            when {
                1 < 2 -> ensure { RuntimeException("This should fail") }
                else -> 3L
            }.toInt()
        }
    }
}