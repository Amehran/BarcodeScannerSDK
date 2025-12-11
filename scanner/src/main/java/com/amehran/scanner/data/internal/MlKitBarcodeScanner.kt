package com.amehran.scanner.data.internal

import android.graphics.Bitmap
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.domain.model.BarcodeType
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MlKitBarcodeScanner @Inject constructor(
    private val mlKitScanner: com.google.mlkit.vision.barcode.BarcodeScanner
) : BarcodeScanner {
    override fun processImage(imageBitmap: Bitmap): Flow<Result<List<BarcodeResult>>> {
        return callbackFlow {
            val image = InputImage.fromBitmap(imageBitmap, 0)
            mlKitScanner.process(image)
                .addOnSuccessListener { mlKitBarcodes ->
                    val domainBarcodes = mlKitBarcodes.mapNotNull { it?.toDomain() }
                    trySend(Result.success(domainBarcodes))
                    close()
                }
                .addOnFailureListener { exception ->
                    // As per AGENTS.md, propagate errors via the Result type, not logs.
                    trySend(Result.failure(exception))
                    close()
                }
            awaitClose()
        }
    }

    override fun release() {
        try {
            mlKitScanner.close()
        } catch (e: Exception) {
            // Per AGENTS.md, avoid excessive logging. The SDK should not crash the host app
            // if it fails to release a resource, so we swallow the exception. A more advanced
            // implementation might use a configurable logger.
        }
    }

    private fun Barcode.toDomain(): BarcodeResult? {
        return BarcodeResult(
            rawValue = this.rawValue ?: return null,
            format = BarcodeFormat.fromMlKitFormat(this.format),
            type = BarcodeType.fromMlKitType(this.valueType),
            displayValue = this.displayValue,
        )
    }
}
