package com.amehran.scanner


import com.amehran.scanner.data.mapper.toDomain
import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeType
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert
import org.junit.Test
import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode
import com.google.common.truth.Truth.assertThat
class BarcodeMapperTest {

    @Test
    fun `toDomain maps rawValue correctly`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { rawValue } returns "TestValue123"
            every { format } returns MlKitBarcode.FORMAT_QR_CODE // Provide a default
            every { valueType } returns MlKitBarcode.TYPE_TEXT // Provide a default
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result.rawValue).isEqualTo("TestValue123")
    }

    // --- Test Format Mapping ---
    @Test
    fun `toDomain maps FORMAT_QR_CODE correctly`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { format } returns MlKitBarcode.FORMAT_QR_CODE
            every { valueType } returns MlKitBarcode.TYPE_TEXT // Default
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result.format).isEqualTo(BarcodeFormat.QR_CODE)
    }

    @Test
    fun `toDomain maps FORMAT_EAN_13 correctly`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { format } returns MlKitBarcode.FORMAT_EAN_13
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result.format).isEqualTo(BarcodeFormat.EAN_13)
    }

    // Add similar tests for ALL other BarcodeFormat mappings...
    // e.g., FORMAT_EAN_8, FORMAT_UPC_A, etc.

    @Test
    fun `toDomain maps unknown format to UNKNOWN`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { format } returns -1 // An undefined format value
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result.format).isEqualTo(BarcodeFormat.UNKNOWN)
    }


    // --- Test Type Mapping ---
    @Test
    fun `toDomain maps TYPE_TEXT correctly`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { valueType } returns MlKitBarcode.TYPE_TEXT
            every { format } returns MlKitBarcode.FORMAT_QR_CODE // Default
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result.type).isEqualTo(BarcodeType.TEXT)
    }

    @Test
    fun `toDomain maps TYPE_URL correctly`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { valueType } returns MlKitBarcode.TYPE_URL
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result.type).isEqualTo(BarcodeType.URL)
    }

    // Add similar tests for ALL other BarcodeType mappings...
    // e.g., TYPE_WIFI, TYPE_CONTACT_INFO, etc.

    @Test
    fun `toDomain maps unknown type to UNKNOWN`() {
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { valueType } returns -1 // An undefined type value
        }
        val result = mockMlKitBarcode.toDomain()
        assertThat(result.type).isEqualTo(BarcodeType.UNKNOWN)
    }

    // Example combining multiple properties
    @Test
    fun `toDomain maps all properties correctly for a complete QR Code URL`() {
        val expectedRawValue = "https://example.com"
        val mockMlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { rawValue } returns expectedRawValue
            every { format } returns MlKitBarcode.FORMAT_QR_CODE
            every { valueType } returns MlKitBarcode.TYPE_URL
            // every { boundingBox } returns mockk() // If you were mapping boundingBox
        }

        val domainResult = mockMlKitBarcode.toDomain()

        assertThat(domainResult.rawValue).isEqualTo(expectedRawValue)
        assertThat(domainResult.format).isEqualTo(BarcodeFormat.QR_CODE)
        assertThat(domainResult.type).isEqualTo(BarcodeType.URL)
    }
}