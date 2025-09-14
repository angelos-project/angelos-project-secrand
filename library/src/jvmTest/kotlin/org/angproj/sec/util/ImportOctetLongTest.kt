package org.angproj.sec.util

import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.Test

class ImportOctetLongTest {

    @Mock
    var importOctetMock: ImportOctetLong = mock()

    @Test
    fun testImportOctetLong() {
        val longs = LongArray(10)
        val readOctetMock: LongArray.(Int) -> Long = mock()

        importOctetMock.importLongs(longs, 0, longs.size, readOctetMock)
        verifyNoMoreInteractions(readOctetMock)
    }
}