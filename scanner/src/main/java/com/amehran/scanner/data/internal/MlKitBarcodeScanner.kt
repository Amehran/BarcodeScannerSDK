package com.amehran.scanner.data.internal // Or your actual package

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.amehran.scanner.data.mapper.BarcodeMapper
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
    private val applicationContext: Context,
    private val mlKitScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    private val barcodeMapper: BarcodeMapper
) : BarcodeScanner { // <-- ADD @Inject HERE
    override fun processImage(imageBitmap: Bitmap): Flow<List<BarcodeResult>> {
        return callbackFlow {
            val image = InputImage.fromBitmap(imageBitmap, 0)
            mlKitScanner.process(image)
                .addOnSuccessListener { mlKitBarcodes ->
                    val domainBarcodes =
                        mlKitBarcodes.mapNotNull { it?.toDomain() } // Assuming you have a toDomain() extension
                    trySend(domainBarcodes).isSuccess
                    close()
                }
                .addOnFailureListener { exception ->
                    Log.e("BarcodeScannerSDK", "Scanning failed", exception)
                    trySend(emptyList()) // Or handle error differently
                    close(exception)
                }
            awaitClose { Log.d("BarcodeScannerSDK", "Scan flow for a bitmap closed.") }
        }
    }

    override fun release() {
        try {
            mlKitScanner.close()
        } catch (e: Exception) {
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