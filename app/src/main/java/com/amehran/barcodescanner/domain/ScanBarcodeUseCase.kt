package com.amehran.barcodescanner.domain

import android.graphics.Bitmap
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * A use case that processes a bitmap image to find barcodes.
 *
 * This use case acts as an intermediary between the ViewModel and the scanner module,
 * ensuring that the domain logic is decoupled from the UI and the data source.
 */
interface ScanBarcodeUseCase {
    /**
     * Scans the provided bitmap for barcodes.
     *
     * @param bitmap The image to be scanned.
     * @return A [Flow] that emits a [Result] containing a list of [BarcodeResult] on success,
     * or an exception on failure.
     */
    operator fun invoke(bitmap: Bitmap): Flow<Result<List<BarcodeResult>>>

    /**
     * Releases the resources held by the underlying barcode scanner.
     * This should be called when the scanner is no longer needed to prevent memory leaks.
     */
    fun releaseScanner()
}

class ScanBarcodeUseCaseImpl @Inject constructor(
    private val barcodeScannerService: BarcodeScanner
) : ScanBarcodeUseCase {

    override operator fun invoke(bitmap: Bitmap): Flow<Result<List<BarcodeResult>>> {
        // The service now returns a Flow<Result<...>>, so we can just return it directly.
        // The ViewModel will be responsible for handling the success or failure state.
        return barcodeScannerService.processImage(bitmap)
    }

    override fun releaseScanner() {
        barcodeScannerService.release()
    }
}
