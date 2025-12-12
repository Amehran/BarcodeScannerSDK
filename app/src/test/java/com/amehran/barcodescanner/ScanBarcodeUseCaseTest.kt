package com.amehran.barcodescanner

import android.graphics.Bitmap
import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
import com.amehran.barcodescanner.domain.ScanBarcodeUseCaseImpl
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.domain.model.BarcodeType
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ScanBarcodeUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var mockBarcodeScanner: BarcodeScanner

    private lateinit var scanBarcodeUseCase: ScanBarcodeUseCase

    @Before
    fun setUp() {
        // As per Clean Architecture, we test the implementation against the interface.
        scanBarcodeUseCase = ScanBarcodeUseCaseImpl(mockBarcodeScanner)
    }

    @Test
    fun `invoke WHEN scanner succeeds with barcodes THEN returns success result with barcodes`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val expectedBarcodeResults = listOf(
            BarcodeResult("TestData1", BarcodeFormat.QR_CODE, BarcodeType.TEXT, "Display This 1")
        )
        // Stub the scanner to return a success Result
        every { mockBarcodeScanner.processImage(mockBitmap) } returns flowOf(Result.success(expectedBarcodeResults))

        // Act
        val result = scanBarcodeUseCase(mockBitmap).first() // Collect the Result

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedBarcodeResults)
        coVerify(exactly = 1) { mockBarcodeScanner.processImage(mockBitmap) }
    }

    @Test
    fun `invoke WHEN scanner succeeds with empty list THEN returns success result with empty list`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        // Stub the scanner to return a success Result with an empty list
        every { mockBarcodeScanner.processImage(mockBitmap) } returns flowOf(Result.success(emptyList()))

        // Act
        val result = scanBarcodeUseCase(mockBitmap).first()

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
        coVerify(exactly = 1) { mockBarcodeScanner.processImage(mockBitmap) }
    }

    @Test
    fun `invoke WHEN scanner fails THEN returns failure result`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val expectedException = IOException("Scanner failed")
        // Stub the scanner to return a failure Result
        every { mockBarcodeScanner.processImage(mockBitmap) } returns flowOf(Result.failure(expectedException))

        // Act
        val result = scanBarcodeUseCase(mockBitmap).first()

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
        assertThat(result.exceptionOrNull()).isEqualTo(expectedException)
        coVerify(exactly = 1) { mockBarcodeScanner.processImage(mockBitmap) }
    }

    @Test
    fun `releaseScanner calls release on barcodeScanner`() {
        // Arrange (No specific arrangement needed)

        // Act
        scanBarcodeUseCase.releaseScanner()

        // Assert
        verify(exactly = 1) { mockBarcodeScanner.release() }
    }
}
