package org.angproj.sec.util

import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.Test

class ImportOctetByteTest {

    @Mock
    var importOctetMock: ImportOctetByte = mock()

    @Test
    fun testImportOctetByte() {
        val bytes = ByteArray(10)
        val readOctetMock: ByteArray.(Int) -> Byte = mock()

        importOctetMock.importBytes(bytes, 0, bytes.size, readOctetMock)
        verifyNoMoreInteractions(readOctetMock)
    }
}