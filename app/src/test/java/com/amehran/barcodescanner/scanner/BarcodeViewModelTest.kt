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
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
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
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.Error::class.java)
        assertThat((viewModel.uiState.value as BarcodeScanUiState.Error).message).isEqualTo("No image provided for scanning.")
    }

    @Test
    fun `WHEN use case returns success THEN state is Success`() = runTest {
        val mockBitmap = mockk<Bitmap>()
        val barcodeResult = BarcodeResult("123", BarcodeFormat.QR_CODE, BarcodeType.TEXT, "123")
        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(Result.success(listOf(barcodeResult)))

        viewModel.processBarcodeScan(mockBitmap)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertThat(finalState).isInstanceOf(BarcodeScanUiState.Success::class.java)
        assertThat((finalState as BarcodeScanUiState.Success).barcodes.first()).isEqualTo(barcodeResult)
    }

    @Test
    fun `WHEN same barcode is scanned twice THEN UI state remains the same`() = runTest {
        val mockBitmap = mockk<Bitmap>()
        val barcodeResult = BarcodeResult("123", BarcodeFormat.QR_CODE, BarcodeType.TEXT, "123")
        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(Result.success(listOf(barcodeResult)))

        // First scan
        viewModel.processBarcodeScan(mockBitmap)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val firstState = viewModel.uiState.value

        // Second scan
        viewModel.processBarcodeScan(mockBitmap)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val secondState = viewModel.uiState.value

        assertThat(secondState).isSameInstanceAs(firstState)
    }

    @Test
    fun `WHEN clearScanResult is called THEN state is Idle and can rescan`() = runTest {
        val mockBitmap = mockk<Bitmap>()
        val barcodeResult = BarcodeResult("abc", BarcodeFormat.QR_CODE, BarcodeType.TEXT, "abc")
        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(Result.success(listOf(barcodeResult)))

        // First scan
        viewModel.processBarcodeScan(mockBitmap)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.Success::class.java)

        // Clear result
        viewModel.clearScanResult()
        assertThat(viewModel.uiState.value).isEqualTo(BarcodeScanUiState.Idle)

        // Scan again
        viewModel.processBarcodeScan(mockBitmap)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(BarcodeScanUiState.Success::class.java)
    }

    @Test
    fun `onCleared calls releaseScanner`() {
        val onClearedMethod = ViewModel::class.java.getDeclaredMethod("onCleared")
        onClearedMethod.isAccessible = true
        onClearedMethod.invoke(viewModel)
        verify(exactly = 1) { mockScanBarcodeUseCase.releaseScanner() }
    }
}

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
