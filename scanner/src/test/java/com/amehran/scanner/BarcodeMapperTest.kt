package com.amehran.scanner

import com.amehran.scanner.data.mapper.toDomain
import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeType
import com.google.common.truth.Truth.assertThat
import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class BarcodeMapperTest {

    @Test
    fun `toDomain returns null if rawValue is null`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { rawValue } returns null
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result).isNull()
    }

    @Test
    fun `toDomain maps all properties correctly`() {
        // Arrange
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { rawValue } returns "TestValue123"
            every { displayValue } returns "DisplayValue"
            every { format } returns MlKitBarcode.FORMAT_QR_CODE
            every { valueType } returns MlKitBarcode.TYPE_URL
        }

        // Act
        val result = mockMlKitBarcode.toDomain()

        // Assert
        assertThat(result).isNotNull()
        result?.let {
            assertThat(it.rawValue).isEqualTo("TestValue123")
            assertThat(it.displayValue).isEqualTo("DisplayValue")
            assertThat(it.format).isEqualTo(BarcodeFormat.QR_CODE)
            assertThat(it.type).isEqualTo(BarcodeType.URL)
        }
    }

    @Test
    fun `toDomain maps unknown format to UNKNOWN`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { format } returns -1 // An undefined format value
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result?.format).isEqualTo(BarcodeFormat.UNKNOWN)
    }

    @Test
    fun `toDomain maps unknown type to UNKNOWN`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { valueType } returns -1 // An undefined type value
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result?.type).isEqualTo(BarcodeType.UNKNOWN)
    }
}
