package com.amehran.scanner.data.internal

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.util.Log
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
    override fun processImage(imageBitmap: Bitmap): Flow<List<BarcodeResult>> {
        Log.d("MlKitBarcodeScanner", "processImage CALLED. Bitmap: ${bitmap}")
        return callbackFlow {
            val image = InputImage.fromBitmap(imageBitmap, 0)
            Log.d(
                "MlKitBarcodeScanner",
                "InputImage created. Processing with ML Kit scanner..."
            )
            mlKitScanner.process(image)
                .addOnSuccessListener { mlKitBarcodes ->
                    Log.d(
                        "MlKitBarcodeScanner",
                        "ML Kit onSuccess. Found ${mlKitBarcodes.size} ML Kit barcodes."
                    )
                    val domainBarcodes =
                        mlKitBarcodes.mapNotNull { it?.toDomain() }
                    trySend(domainBarcodes).isSuccess
                    close()
                }
                .addOnFailureListener { exception ->
                    Log.e("BarcodeScannerSDK", "Scanning failed", exception)
                    trySend(emptyList())
                    close(exception)
                }
            awaitClose { Log.d("BarcodeScannerSDK", "Scan flow for a bitmap closed.") }
        }
    }

    override fun release() {
        try {
            mlKitScanner.close()
        } catch (e: Exception) {
            Log.e("MlKitBarcodeScanner", "Error closing ML Kit scanner", e)
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