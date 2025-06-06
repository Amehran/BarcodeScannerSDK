package com.amehran.barcodescanner.domain

import android.graphics.Bitmap
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ScanBarcodeUseCase {
    operator fun invoke(bitmap: Bitmap): Flow<List<BarcodeResult>> // Assuming it returns a Flow
    fun releaseScanner() // Add this method
}

class ScanBarcodeUseCaseImpl @Inject constructor(
    private val barcodeScannerService: BarcodeScanner // Injected from your SDK
) : ScanBarcodeUseCase {

    override operator fun invoke(bitmap: Bitmap): Flow<List<BarcodeResult>> {
        return barcodeScannerService.processImage(bitmap)
            .map { sdkDataList ->
                sdkDataList.map { sdkData ->
                    // Map from SdkBarcodeData (SDK) to BarcodeResult (App UI model)
                    BarcodeResult(
                        rawValue = sdkData.rawValue,
                        displayValue = sdkData.displayValue,
                        format = sdkData.format,
                        type = sdkData.type
                    )
                }
            }
    }

    override fun releaseScanner() {
        barcodeScannerService.release() // Call your SDK's release method
    }
}