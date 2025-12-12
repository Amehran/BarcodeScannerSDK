package com.amehran.scanner.data.internal

import android.graphics.Bitmap
import com.amehran.scanner.data.mapper.toDomain
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeResult
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
                    // Use the dedicated mapper
                    val domainBarcodes = mlKitBarcodes.mapNotNull { it?.toDomain() }
                    trySend(Result.success(domainBarcodes))
                    close()
                }
                .addOnFailureListener { exception ->
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
            // Per AGENTS.md, swallow exception on release to avoid crashing the host app.
        }
    }
}
