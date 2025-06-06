//package com.amehran.barcodescanner.scanner
//
//import android.graphics.Bitmap
//import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
//import com.amehran.barcodescanner.presentation.scanner.BarcodeScanUiState
//import com.amehran.barcodescanner.presentation.scanner.BarcodeViewModel
//import com.amehran.scanner.domain.model.BarcodeResult
//import com.amehran.scanner.domain.model.BarcodeFormat
//import com.amehran.scanner.domain.model.BarcodeType
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.impl.annotations.RelaxedMockK
//import io.mockk.junit4.MockKRule
//import io.mockk.mockk
//import io.mockk.verify
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//// Crucial Flow Imports (ensure kotlinx.coroutines.flow.* is preferred or these specific ones)
//import kotlinx.coroutines.flow.collect // The extension function we need for StateFlow
//import kotlinx.coroutines.flow.flow     // For creating flows in mocks
//import kotlinx.coroutines.flow.flowOf   // For creating flows in mocks
//// Other coroutine imports
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.resetMain
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.After
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import java.io.IOException
//
//@ExperimentalCoroutinesApi
//class BarcodeViewModelTest {
//
//    @get:Rule
//    val mockkRule = MockKRule(this)
//
//    private val testDispatcher = StandardTestDispatcher()
//
//    @RelaxedMockK
//    private lateinit var mockScanBarcodeUseCase: ScanBarcodeUseCase
//
//    private lateinit var viewModel: BarcodeViewModel
//
//    @Before
//    fun setUp() {
//        Dispatchers.setMain(testDispatcher)
//        viewModel = BarcodeViewModel(mockScanBarcodeUseCase)
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun `initial UI state is Idle`() = runTest {
//        assertEquals(BarcodeScanUiState.Idle, viewModel.uiState.value) // .value works on StateFlow too
//    }
//
//    @Test
//    fun `processBarcodeScan with null bitmap sets state to Error`() = runTest {
//        viewModel.processBarcodeScan(null)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        val state = viewModel.uiState.value
//        assertTrue("State should be Error. Was: $state", state is BarcodeScanUiState.Error)
//        if (state is BarcodeScanUiState.Error) {
//            assertEquals("No image provided for scanning.", state.message)
//        }
//    }
//
//    @Test
//    fun `processBarcodeScan success updates UI state from Scanning to Success`() = runTest(testDispatcher) {
//        val mockBitmap = mockk<Bitmap>()
//        val barcodeResults = listOf(
//            BarcodeResult("123", BarcodeFormat.QR_CODE, BarcodeType.TEXT, "Display This 1")
//        )
//        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(barcodeResults)
//
//        val collectedStates = mutableListOf<BarcodeScanUiState>()
//        val collectionJob: Job = launch {
//            viewModel.uiState.collect { state ->
//                collectedStates.add(state)
//            }
//        }
//        testDispatcher.scheduler.advanceUntilIdle() // Collect initial Idle state
//
//        assertEquals("Initial state should be Idle", BarcodeScanUiState.Idle, collectedStates.firstOrNull())
//        assertEquals("Should have collected 1 state (Idle)", 1, collectedStates.size)
//
//
//        viewModel.processBarcodeScan(mockBitmap)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        assertEquals("Should have collected 3 states", 3, collectedStates.size)
//        assertEquals(BarcodeScanUiState.Idle, collectedStates[0])
//        assertEquals(BarcodeScanUiState.Scanning, collectedStates[1])
//        assertEquals(BarcodeScanUiState.Success(barcodeResults), collectedStates[2])
//
//        coVerify(exactly = 1) { mockScanBarcodeUseCase(mockBitmap) }
//        collectionJob.cancel()
//    }
//
//    @Test
//    fun `processBarcodeScan no barcodes found updates UI state to Error`() = runTest(testDispatcher) {
//        val mockBitmap = mockk<Bitmap>()
//        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(emptyList<BarcodeResult>())
//
//        val collectedStates = mutableListOf<BarcodeScanUiState>()
//        val collectionJob = launch { viewModel.uiState.collect { collectedStates.add(it) } } // THIS SHOULD NOW WORK!
//        testDispatcher.scheduler.advanceUntilIdle() // initial Idle
//
//        viewModel.processBarcodeScan(mockBitmap)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        assertEquals(3, collectedStates.size)
//        assertEquals(BarcodeScanUiState.Idle, collectedStates[0])
//        assertEquals(BarcodeScanUiState.Scanning, collectedStates[1])
//        val errorState = collectedStates[2]
//        assertTrue("State should be Error. Was: $errorState", errorState is BarcodeScanUiState.Error)
//        if (errorState is BarcodeScanUiState.Error) {
//            assertEquals("No barcodes found in the image.", errorState.message)
//        }
//
//        coVerify(exactly = 1) { mockScanBarcodeUseCase(mockBitmap) }
//        collectionJob.cancel()
//    }
//
//    @Test
//    fun `processBarcodeScan use case throws exception updates UI state to Error`() = runTest(testDispatcher) {
//        val mockBitmap = mockk<Bitmap>()
//        val errorMessage = "Scanner exploded"
//        val exception = IOException(errorMessage)
//        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flow { throw exception }
//
//        val collectedStates = mutableListOf<BarcodeScanUiState>()
//        val collectionJob = launch { viewModel.uiState.collect { collectedStates.add(it) } } // THIS SHOULD NOW WORK!
//        testDispatcher.scheduler.advanceUntilIdle() // initial Idle
//
//        viewModel.processBarcodeScan(mockBitmap)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        assertEquals(3, collectedStates.size)
//        assertEquals(BarcodeScanUiState.Idle, collectedStates[0])
//        assertEquals(BarcodeScanUiState.Scanning, collectedStates[1])
//        val errorState = collectedStates[2]
//        assertTrue("State should be Error. Was: $errorState", errorState is BarcodeScanUiState.Error)
//        if (errorState is BarcodeScanUiState.Error) {
//            assertEquals(errorMessage, errorState.message)
//        }
//        coVerify(exactly = 1) { mockScanBarcodeUseCase(mockBitmap) }
//        collectionJob.cancel()
//    }
//
//    @Test
//    fun `clearScanResult resets UI state to Idle`() = runTest(testDispatcher) {
//        val mockBitmap = mockk<Bitmap>()
//        val barcodeResults = listOf(
//            BarcodeResult("Test Data", BarcodeFormat.CODE_128, BarcodeType.TEXT, "Display for Clear")
//        )
//        coEvery { mockScanBarcodeUseCase(mockBitmap) } returns flowOf(barcodeResults)
//
//        var collectionJobInitial: Job? = null
//        try {
//            val initialStates = mutableListOf<BarcodeScanUiState>()
//            collectionJobInitial = launch { viewModel.uiState.collect { initialStates.add(it) } } // THIS SHOULD NOW WORK!
//            testDispatcher.scheduler.advanceUntilIdle()
//
//            viewModel.processBarcodeScan(mockBitmap)
//            testDispatcher.scheduler.advanceUntilIdle()
//            assertEquals(BarcodeScanUiState.Success(barcodeResults), initialStates.lastOrNull())
//        } finally {
//            collectionJobInitial?.cancel()
//        }
//
//        val collectedStatesAfterClear = mutableListOf<BarcodeScanUiState>()
//        val collectionJobClear = launch { viewModel.uiState.collect { collectedStatesAfterClear.add(it) } } // THIS SHOULD NOW WORK!
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        viewModel.clearScanResult()
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        assertTrue("Should have collected at least one state after clear trigger", collectedStatesAfterClear.isNotEmpty())
//        assertEquals("Last collected state should be Idle", BarcodeScanUiState.Idle, collectedStatesAfterClear.lastOrNull())
//        assertEquals("Current uiState value should be Idle", BarcodeScanUiState.Idle, viewModel.uiState.value)
//
//        collectionJobClear.cancel()
//    }
//
//    @Test
//    fun `onCleared calls releaseScanner on use case`() {
//        viewModel.onCleared()
//        verify(exactly = 1) { mockScanBarcodeUseCase.releaseScanner() }
//    }
//}