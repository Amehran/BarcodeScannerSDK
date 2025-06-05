package com.amehran.barcodescanner.domain

import android.graphics.Bitmap
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ScanBarcodeUseCase @Inject constructor(
    private val barcodeScanner: BarcodeScanner // Hilt injects the implementation here
) {

    operator fun invoke(imageBitmap: Bitmap): Flow<List<BarcodeResult>> {
        return barcodeScanner.processImage(imageBitmap)
    }


    fun releaseScanner() {
        barcodeScanner.release()
    }
}