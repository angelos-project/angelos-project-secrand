package org.angproj.sec.util

import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.Test

class ExportOctetLongTest {

    @Mock
    var exportOctetMock: ExportOctetLong = mock()

    @Test
    fun testExportOctetByte() {
        val longs = LongArray(10)
        val writeOctetMock: LongArray.(Int, Long) -> Unit = mock()

        exportOctetMock.exportLongs(longs, 0, longs.size, writeOctetMock)
        verifyNoMoreInteractions(writeOctetMock)
    }
}