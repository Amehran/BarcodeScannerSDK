package com.amehran.barcodescanner.presentation.scanner

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
import com.amehran.scanner.domain.model.BarcodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject



// Represents the different states the UI can be in
sealed class BarcodeScanUiState {
    object Idle : BarcodeScanUiState() // Initial state, or after a scan is cleared
    object Scanning : BarcodeScanUiState() // Actively scanning an image
    data class Success(val barcodes: List<BarcodeResult>) : BarcodeScanUiState() // Scan successful
    data class Error(val message: String) : BarcodeScanUiState() // Scan failed
}

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val scanBarcodeUseCase: ScanBarcodeUseCase
) : ViewModel() {

    // Private mutable state for the UI
    private val _uiState = mutableStateOf<BarcodeScanUiState>(BarcodeScanUiState.Idle)
    // Public immutable State for the UI to observe
    val uiState: State<BarcodeScanUiState> = _uiState

    /**
     * Processes the given bitmap to scan for barcodes.
     * Updates the uiState based on the scanning result.
     */
    fun processBarcodeScan(bitmap: Bitmap?) {
        if (bitmap == null) {
            _uiState.value = BarcodeScanUiState.Error("No image provided for scanning.")
            return
        }

        _uiState.value = BarcodeScanUiState.Scanning
        viewModelScope.launch {
            scanBarcodeUseCase(bitmap) // Invoke the use case
                .catch { exception ->
                    // Handle errors from the Flow
                    _uiState.value = BarcodeScanUiState.Error(exception.message ?: "Unknown scanning error")
                }
                .collect { barcodes ->
                    // Handle successful results from the Flow
                    if (barcodes.isNotEmpty()) {
                        _uiState.value = BarcodeScanUiState.Success(barcodes)
                    } else {
                        _uiState.value = BarcodeScanUiState.Error("No barcodes found in the image.")
                    }
                }
        }
    }

    /**
     * Resets the UI state back to Idle.
     * Call this when the user dismisses results or wants to scan a new image.
     */
    fun clearScanResult() {
        _uiState.value = BarcodeScanUiState.Idle
    }

    /**
     * Called when the ViewModel is about to be cleared.
     * This is a good place to release resources held by the use case.
     */
    override fun onCleared() {
        super.onCleared()
        scanBarcodeUseCase.releaseScanner() // Release scanner resources
    }
}