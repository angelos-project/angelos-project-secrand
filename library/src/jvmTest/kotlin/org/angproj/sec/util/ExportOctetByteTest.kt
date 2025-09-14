package org.angproj.sec.util

import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.Test

class ExportOctetByteTest {

    @Mock
    var exportOctetMock: ExportOctetByte = mock()

    @Test
    fun testExportOctetByte() {
        val bytes = ByteArray(10)
        val writeOctetMock: ByteArray.(Int, Byte) -> Unit = mock()

        exportOctetMock.exportBytes(bytes, 0, bytes.size, writeOctetMock)
        verifyNoMoreInteractions(writeOctetMock)
    }
}