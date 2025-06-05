package com.amehran.barcodescanner // Adjust to your app's package name

import android.graphics.Bitmap
import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ScanBarcodeUseCaseTest {

    // Rule to automatically initialize mocks annotated with @MockK, @RelaxedMockK, etc.
    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK // Creates a relaxed mock for BarcodeScanner
    private lateinit var mockBarcodeScanner: BarcodeScanner

    private lateinit var scanBarcodeUseCase: ScanBarcodeUseCase

    @Before
    fun setUp() {
        scanBarcodeUseCase = ScanBarcodeUseCase(mockBarcodeScanner)
    }

    @Test
    fun `invoke calls processImage on barcodeScanner and returns its result`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>() // Relaxed mock for Bitmap
        val expectedBarcodeResults = listOf(
            BarcodeResult("TestData1", BarcodeFormat.QR_CODE, BarcodeType.TEXT, null),
            BarcodeResult("TestData2", BarcodeFormat.EAN_13, BarcodeType.PRODUCT, null)
        )
        // Stub the behavior of mockBarcodeScanner.processImage()
        every { mockBarcodeScanner.processImage(mockBitmap) } returns flowOf(expectedBarcodeResults)

        // Act
        val resultFlow = scanBarcodeUseCase(mockBitmap)
        val actualBarcodeResults = resultFlow.first() // Collect the first emission

        // Assert
        assertThat(actualBarcodeResults).isEqualTo(expectedBarcodeResults)
        // Verify that processImage was called exactly once with the mockBitmap
        coVerify(exactly = 1) { mockBarcodeScanner.processImage(mockBitmap) }
    }

    @Test
    fun `invoke propagates error from barcodeScanner`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val expectedException = IOException("Scanner failed")
        // Stub the behavior to return a flow that emits an error
        every { mockBarcodeScanner.processImage(mockBitmap) } returns flow { throw expectedException }

        // Act & Assert
        try {
            scanBarcodeUseCase(mockBitmap).first() // Attempt to collect, which should throw
            assert(false) { "Expected an exception to be thrown" } // Fail if no exception
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(IOException::class.java)
            assertThat(e).isEqualTo(expectedException)
        }
        coVerify(exactly = 1) { mockBarcodeScanner.processImage(mockBitmap) }
    }

    @Test
    fun `releaseScanner calls release on barcodeScanner`() {
        // Arrange (No specific arrangement needed as we're just verifying a call)

        // Act
        scanBarcodeUseCase.releaseScanner()

        // Assert
        // Verify that release was called exactly once on the mockBarcodeScanner
        verify(exactly = 1) { mockBarcodeScanner.release() }
    }
}