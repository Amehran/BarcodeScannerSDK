package com.amehran.scanner

import android.graphics.Bitmap
import com.amehran.scanner.data.internal.MlKitBarcodeScanner
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth
import com.google.mlkit.vision.barcode.BarcodeScanner as MlKitSdkScanner
import com.google.mlkit.vision.barcode.BarcodeScanning // Keep this import
import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode
import com.google.mlkit.vision.common.InputImage
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE, sdk = [28])
class MlKitBarcodeScannerTest {

    private lateinit var mlKitSdkScannerMock: MlKitSdkScanner
    private lateinit var SUT: com.amehran.scanner.domain.BarcodeScanner

    // ... (mlKitSdkScannerMock, SUT)

    // Add InputImage to the list of classes whose static methods you'll mock
    @Before
    fun setUp() {
        mlKitSdkScannerMock = mockk(relaxed = true)

        mockkStatic(BarcodeScanning::class)
        every { BarcodeScanning.getClient(any()) } returns mlKitSdkScannerMock
        every { BarcodeScanning.getClient() } returns mlKitSdkScannerMock

        // ---- ADD THIS SECTION FOR InputImage ----
        mockkStatic(InputImage::class) // Enable static mocking for InputImage
        val mockInputImage = mockk<InputImage>(relaxed = true) // Create a relaxed mock for InputImage instances
        // Mock the specific static method fromBitmap
        // "any()" for bitmap and rotationDegrees as we don't care about their specific values in this mock
        every { InputImage.fromBitmap(any(), any()) } returns mockInputImage
        // ------------------------------------------

        SUT = MlKitBarcodeScanner(context.applicationContext, mlKitInstance, mapper)
    }

    @After
    fun tearDown() {
        unmockkStatic(BarcodeScanning::class)
        unmockkStatic(InputImage::class) // ---- UNMOCK InputImage ----
        clearAllMocks()
    }
    // ... (rest of your test methods: mockTaskForProcess, processImage tests, etc.)
    // Your mockTaskForProcess and the actual test methods using it should remain the same.
    // The key was just removing the MockedStatic variable and ensuring mockkStatic/unmockkStatic are used correctly.
    private fun mockTaskForProcess(barcodesToReturn: List<MlKitBarcode>? = null, exceptionToThrow: Exception? = null): Task<List<MlKitBarcode>> {
        val taskMock = mockk<Task<List<MlKitBarcode>>>(relaxed = true)

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
        every { taskMock.isSuccessful } returns (barcodesToReturn != null && exceptionToThrow == null)
        return taskMock
    }


    @Test
    fun `processImage successfully detects and maps barcodes`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val rawValue1 = "QRData1"
        val mlKitBarcode1 = mockk<MlKitBarcode>(relaxed = true) {
            every { rawValue } returns rawValue1
            every { format } returns MlKitBarcode.FORMAT_QR_CODE
            every { valueType } returns MlKitBarcode.TYPE_TEXT
        }
        val mlKitBarcodes = listOf(mlKitBarcode1)
        val successTask = mockTaskForProcess(barcodesToReturn = mlKitBarcodes)
        every { mlKitSdkScannerMock.process(any<InputImage>()) } returns successTask

        val actualResults = SUT.processImage(mockBitmap).first()

        Truth.assertThat(actualResults).hasSize(1)
        Truth.assertThat(actualResults[0].rawValue).isEqualTo(rawValue1)
        verify { mlKitSdkScannerMock.process(any<InputImage>()) }
    }

    @Test
    fun `processImage returns empty list when no barcodes detected`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val emptyMlKitBarcodes = emptyList<MlKitBarcode>()
        val successTask = mockTaskForProcess(barcodesToReturn = emptyMlKitBarcodes)
        every { mlKitSdkScannerMock.process(any<InputImage>()) } returns successTask

        val actualResults = SUT.processImage(mockBitmap).first()
        Truth.assertThat(actualResults).isEmpty()
    }

    @Test
    fun `processImage propagates failure from ML Kit`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val expectedException = IOException("ML Kit processing failed")
        val failureTask = mockTaskForProcess(exceptionToThrow = expectedException)
        every { mlKitSdkScannerMock.process(any<InputImage>()) } returns failureTask

        try {
            SUT.processImage(mockBitmap).first()
            assert(false) { "Flow should have thrown an exception" }
        } catch (e: Exception) {
            Truth.assertThat(e).isInstanceOf(IOException::class.java)
            Truth.assertThat(e).hasMessageThat().isEqualTo(expectedException.message)
        }
    }

    @Test
    fun `release does not throw an exception (basic check)`() {
        try {
            SUT.release()
        } catch (e: Exception) {
            assert(false) { "SUT.release() should not throw an exception: $e" }
        }
    }
}