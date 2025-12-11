package com.amehran.barcodescanner.scanner

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
import com.amehran.barcodescanner.presentation.scanner.BarcodeScanUiState
import com.amehran.barcodescanner.presentation.scanner.BarcodeViewModel
import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.domain.model.BarcodeType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class BarcodeViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    // A reusable rule for managing the Main dispatcher in tests
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @RelaxedMockK
    private lateinit var mockScanBarcodeUseCase: ScanBarcodeUseCase

    private lateinit var viewModel: BarcodeViewModel

    @Before
    fun setUp() {
        viewModel = BarcodeViewModel(mockScanBarcodeUseCase)
    }

    @Test
    fun `initial UI state is Idle`() {
        assertThat(viewModel.uiState.value).isEqualTo(BarcodeScanUiState.Idle)
    }

    @Test
    fun `processBarcodeScan with null bitmap sets state to Error`() = runTest {
        viewModel.processBarcodeScan(null)
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(BarcodeScanUiState.Error::class.java)
        assertThat((state as BarcodeScanUiState.Error).message).isEqualTo("No image provided for scanning.")
    }

    @Test
    fun `WHEN use case returns success with barcodes THEN state transitions to Success`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val barcodeResults = listOf(BarcodeResult("123", BarcodeFormat.QR_CODE, BarcodeType.TEXT, "123"))
        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(Result.success(barcodeResults))

        // Act
        viewModel.processBarcodeScan(mockBitmap)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.Scanning::class.java)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertThat(finalState).isInstanceOf(BarcodeScanUiState.Success::class.java)
        assertThat((finalState as BarcodeScanUiState.Success).barcodes).isEqualTo(barcodeResults)
        coVerify(exactly = 1) { mockScanBarcodeUseCase(mockBitmap) }
    }

    @Test
    fun `WHEN use case returns success with empty list THEN state transitions to NoBarcodesFound`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(Result.success(emptyList()))

        // Act
        viewModel.processBarcodeScan(mockBitmap)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.Scanning::class.java)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.NoBarcodesFound::class.java)
        coVerify(exactly = 1) { mockScanBarcodeUseCase(mockBitmap) }
    }

    @Test
    fun `WHEN use case returns failure THEN state transitions to Error`() = runTest {
        // Arrange
        val mockBitmap = mockk<Bitmap>()
        val errorMessage = "Scanner exploded"
        val exception = IOException(errorMessage)
        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(Result.failure(exception))

        // Act
        viewModel.processBarcodeScan(mockBitmap)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.Scanning::class.java)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val finalState = viewModel.uiState.value
        assertThat(finalState).isInstanceOf(BarcodeScanUiState.Error::class.java)
        assertThat((finalState as BarcodeScanUiState.Error).message).isEqualTo(errorMessage)
        coVerify(exactly = 1) { mockScanBarcodeUseCase(mockBitmap) }
    }

    @Test
    fun `clearScanResult resets state to Idle`() = runTest {
        // Set a non-idle state first
        val mockBitmap = mockk<Bitmap>()
        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(Result.success(listOf(mockk())))
        viewModel.processBarcodeScan(mockBitmap)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.Success::class.java)

        // Act
        viewModel.clearScanResult()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(BarcodeScanUiState.Idle)
    }

    @Test
    fun `onCleared calls releaseScanner on use case`() {
        // This is a bit tricky to test with Hilt, but we can call it directly.
        // In a real app, this is called by the ViewModel lifecycle.
        val viewModel = BarcodeViewModel(mockScanBarcodeUseCase)
        val onClearedMethod = ViewModel::class.java.getDeclaredMethod("onCleared")
        onClearedMethod.isAccessible = true
        onClearedMethod.invoke(viewModel)

        verify(exactly = 1) { mockScanBarcodeUseCase.releaseScanner() }
    }
}

// A JUnit Rule to setup and teardown the main dispatcher for tests.
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : org.junit.rules.TestWatcher() {
    override fun starting(description: org.junit.runner.Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: org.junit.runner.Description) {
        Dispatchers.resetMain()
    }
}
