package com.amehran.barcodescanner.presentation.scanner

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
import com.amehran.scanner.domain.model.BarcodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Defines the possible states for the barcode scanning UI.
 */
sealed class BarcodeScanUiState {
    object Idle : BarcodeScanUiState()
    object Scanning : BarcodeScanUiState()
    data class Success(val barcodes: List<BarcodeResult>) : BarcodeScanUiState()
    object NoBarcodesFound : BarcodeScanUiState()
    data class Error(val message: String) : BarcodeScanUiState()
}

/**
 * The ViewModel for the barcode scanning screen.
 *
 * It processes images from the camera, manages the UI state, and ensures that the UI is only
 * updated with *new* barcode results to create a fluid, continuous scanning experience.
 */
@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val scanBarcodeUseCase: ScanBarcodeUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf<BarcodeScanUiState>(BarcodeScanUiState.Idle)
    val uiState: State<BarcodeScanUiState> = _uiState

    // Keep track of the last successful barcode to avoid redundant UI updates.
    private var lastScannedBarcode: String? = null

    /**
     * Processes a bitmap from the continuous camera stream to scan for barcodes.
     *
     * To ensure a smooth user experience, this function implements a smart-update mechanism. The UI
     * state will only be updated to [BarcodeScanUiState.Success] if the detected barcode's value
     * is different from the previously scanned one.
     *
     * This prevents the UI from getting stuck on a single result during continuous scanning.
     *
     * @param bitmap The image to be scanned. If null, the state will be set to [BarcodeScanUiState.Error].
     */
    fun processBarcodeScan(bitmap: Bitmap?) {
        if (bitmap == null) {
            _uiState.value = BarcodeScanUiState.Error("No image provided for scanning.")
            return
        }

        // Set to scanning only if we are not already showing a success state.
        if (_uiState.value !is BarcodeScanUiState.Success) {
            _uiState.value = BarcodeScanUiState.Scanning
        }

        viewModelScope.launch {
            scanBarcodeUseCase(bitmap)
                .collect { result ->
                    result.onSuccess { barcodes ->
                        val newBarcode = barcodes.firstOrNull()
                        if (newBarcode != null) {
                            // Only update the UI if the barcode is new.
                            if (newBarcode.rawValue != lastScannedBarcode) {
                                lastScannedBarcode = newBarcode.rawValue
                                _uiState.value = BarcodeScanUiState.Success(barcodes)
                            }
                        } else {
                            _uiState.value = BarcodeScanUiState.NoBarcodesFound
                        }
                    }.onFailure { exception ->
                        _uiState.value =
                            BarcodeScanUiState.Error(exception.message ?: "Unknown scanning error")
                    }
                }
        }
    }

    /**
     * Resets the UI state back to [BarcodeScanUiState.Idle] and clears the last scanned barcode.
     * This allows the user to manually trigger a re-scan of the same barcode if needed.
     */
    fun clearScanResult() {
        _uiState.value = BarcodeScanUiState.Idle
        lastScannedBarcode = null
    }

    override fun onCleared() {
        super.onCleared()
        scanBarcodeUseCase.releaseScanner()
    }
}
