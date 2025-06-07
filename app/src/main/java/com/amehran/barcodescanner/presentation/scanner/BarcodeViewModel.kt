package com.amehran.barcodescanner.presentation.scanner

import android.graphics.Bitmap
import android.util.Log
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

sealed class BarcodeScanUiState {
    object Idle : BarcodeScanUiState()
    object Scanning : BarcodeScanUiState()
    data class Success(val barcodes: List<BarcodeResult>) : BarcodeScanUiState()
    data class Error(val message: String) : BarcodeScanUiState()
}

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val scanBarcodeUseCase: ScanBarcodeUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf<BarcodeScanUiState>(BarcodeScanUiState.Idle)

    val uiState: State<BarcodeScanUiState> = _uiState

    fun processBarcodeScan(bitmap: Bitmap?) {
        if (bitmap == null) {
            Log.d("BarcodeViewModel", "Bitmap is null")
            _uiState.value = BarcodeScanUiState.Error("No image provided for scanning.")
            return
        }

        _uiState.value = BarcodeScanUiState.Scanning
        viewModelScope.launch {
            Log.d("BarcodeViewModel", "Bitmap is ${bitmap}")

            scanBarcodeUseCase(bitmap) // Invoke the use case
                .catch { exception ->
                    _uiState.value =
                        BarcodeScanUiState.Error(exception.message ?: "Unknown scanning error")
                }
                .collect { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        Log.d("BarcodeViewModel", "Barcode is empty7")

                        _uiState.value = BarcodeScanUiState.Success(barcodes)
                    } else {
                        Log.d("BarcodeViewModel", "No barcodes found in the image")

                        _uiState.value = BarcodeScanUiState.Error("No barcodes found in the image.")
                    }
                }
        }
    }

    fun clearScanResult() {
        _uiState.value = BarcodeScanUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        scanBarcodeUseCase.releaseScanner()
    }
}