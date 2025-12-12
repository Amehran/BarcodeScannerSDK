package com.amehran.scanner

import android.graphics.Bitmap
import com.amehran.scanner.data.internal.MlKitBarcodeScanner
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode
import com.google.mlkit.vision.common.InputImage
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MlKitBarcodeScannerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var mlKitSdkScanner: com.google.mlkit.vision.barcode.BarcodeScanner

    private lateinit var sut: MlKitBarcodeScanner

    @Before
    fun setUp() {
        // Mock the static call to InputImage.fromBitmap, which is a dependency of our SUT.
        mockkStatic(InputImage::class)
        every { InputImage.fromBitmap(any(), any()) } returns mockk()

        sut = MlKitBarcodeScanner(mlKitSdkScanner)
    }

    @After
    fun tearDown() {
        unmockkStatic(InputImage::class)
        clearAllMocks()
    }

    @Test
    fun `WHEN process succeeds with barcodes THEN returns success result`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val mlKitBarcode = mockk<MlKitBarcode>(relaxed = true) {
            every { rawValue } returns "RawValue"
        }
        val successTask = mockTaskForProcess(barcodesToReturn = listOf(mlKitBarcode))
        every { mlKitSdkScanner.process(any<InputImage>()) } returns successTask

        // Act
        val result = sut.processImage(mockBitmap).first()

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(1)
        assertThat(result.getOrNull()?.first()?.rawValue).isEqualTo("RawValue")
        verify(exactly = 1) { mlKitSdkScanner.process(any<InputImage>()) }
    }

    @Test
    fun `WHEN process succeeds with no barcodes THEN returns success with empty list`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val successTask = mockTaskForProcess(barcodesToReturn = emptyList())
        every { mlKitSdkScanner.process(any<InputImage>()) } returns successTask

        // Act
        val result = sut.processImage(mockBitmap).first()

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `WHEN process fails THEN returns failure result`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val expectedException = IOException("ML Kit failed")
        val failureTask = mockTaskForProcess(exceptionToThrow = expectedException)
        every { mlKitSdkScanner.process(any<InputImage>()) } returns failureTask

        // Act
        val result = sut.processImage(mockBitmap).first()

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
        assertThat(result.exceptionOrNull()).isEqualTo(expectedException)
    }

    @Test
    fun `release() calls close on the underlying scanner`() {
        // Act
        sut.release()

        // Assert
        verify(exactly = 1) { mlKitSdkScanner.close() }
    }

    /**
     * Helper function to mock the Task<T> API used by ML Kit.
     */
    private fun mockTaskForProcess(
        barcodesToReturn: List<MlKitBarcode>? = null,
        exceptionToThrow: Exception? = null
    ): Task<List<MlKitBarcode>> {
        val taskMock = mockk<Task<List<MlKitBarcode>>>()
        every { taskMock.addOnSuccessListener(any()) } answers {
            if (barcodesToReturn != null) {
                (firstArg() as OnSuccessListener<List<MlKitBarcode>>).onSuccess(barcodesToReturn)
            }
            taskMock
        }

        every { taskMock.addOnFailureListener(any()) } answers {
            if (exceptionToThrow != null) {
                (firstArg() as OnFailureListener).onFailure(exceptionToThrow)
            }
            taskMock
        }
        return taskMock
    }
}
