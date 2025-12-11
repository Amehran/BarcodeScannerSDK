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
    /** The initial state, before any scanning has occurred. */
    object Idle : BarcodeScanUiState()

    /** The state while the scanner is actively processing an image. */
    object Scanning : BarcodeScanUiState()

    /** The state when barcodes have been successfully found. */
    data class Success(val barcodes: List<BarcodeResult>) : BarcodeScanUiState()

    /** The state when no barcodes are found in the scanned image. */
    object NoBarcodesFound : BarcodeScanUiState()

    /** The state when an error occurs during scanning. */
    data class Error(val message: String) : BarcodeScanUiState()
}

/**
 * The ViewModel responsible for the business logic of the barcode scanning screen.
 *
 * It communicates with the domain layer ([ScanBarcodeUseCase]) to process images and updates the
 * UI state accordingly.
 */
@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val scanBarcodeUseCase: ScanBarcodeUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf<BarcodeScanUiState>(BarcodeScanUiState.Idle)

    /** The observable state of the barcode scanning UI. */
    val uiState: State<BarcodeScanUiState> = _uiState

    /**
     * Processes the given bitmap to scan for barcodes.
     *
     * It updates the [uiState] to reflect the current status of the operation:
     * [BarcodeScanUiState.Scanning], [BarcodeScanUiState.Success],
     * [BarcodeScanUiState.NoBarcodesFound], or [BarcodeScanUiState.Error].
     *
     * @param bitmap The image to be scanned. If null, the state will be set to [BarcodeScanUiState.Error].
     */
    fun processBarcodeScan(bitmap: Bitmap?) {
        if (bitmap == null) {
            _uiState.value = BarcodeScanUiState.Error("No image provided for scanning.")
            return
        }

        _uiState.value = BarcodeScanUiState.Scanning
        viewModelScope.launch {
            scanBarcodeUseCase(bitmap) // This now returns a Flow<Result<...>>
                .collect { result ->
                    result.onSuccess { barcodes ->
                        _uiState.value = if (barcodes.isNotEmpty()) {
                            BarcodeScanUiState.Success(barcodes)
                        } else {
                            BarcodeScanUiState.NoBarcodesFound
                        }
                    }.onFailure { exception ->
                        _uiState.value =
                            BarcodeScanUiState.Error(exception.message ?: "Unknown scanning error")
                    }
                }
        }
    }

    /**
     * Resets the UI state back to [BarcodeScanUiState.Idle].
     */
    fun clearScanResult() {
        _uiState.value = BarcodeScanUiState.Idle
    }

    /**
     * Releases the underlying scanner resources when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        scanBarcodeUseCase.releaseScanner()
    }
}
