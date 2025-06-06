package com.amehran.scanner.data.internal // Or your actual package

import android.content.Context // Example: If you needed context
import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.amehran.scanner.domain.BarcodeScanner // Your domain interface
import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.domain.model.BarcodeType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject // <-- IMPORT THIS

class MlKitBarcodeScanner @Inject constructor() : BarcodeScanner { // <-- ADD @Inject HERE

    // If your MlKitBarcodeScanner needs NO constructor parameters,
    // the empty constructor() is fine.

    // Example: If you decided it needed ApplicationContext:
    // class MlKitBarcodeScanner @Inject constructor(
    //     private val applicationContext: Context
    // ) : BarcodeScanner { ... }
    // (If you did this, Hilt would provide the ApplicationContext.
    //  You'd also need to ensure Context is available where this is scoped,
    //  e.g., via @ApplicationContext annotation for the parameter and
    //  ensuring the module installs in SingletonComponent or similar).
    //  For now, stick with the empty constructor if that's all you need.

    private val mlKitOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_PDF417
        )
        .build()

    private val mlKitScanner = BarcodeScanning.getClient(mlKitOptions)

    override fun processImage(imageBitmap: Bitmap): Flow<List<BarcodeResult>> {
        return callbackFlow {
            val image = InputImage.fromBitmap(imageBitmap, 0)
            mlKitScanner.process(image)
                .addOnSuccessListener { mlKitBarcodes ->
                    val domainBarcodes = mlKitBarcodes.mapNotNull { it?.toDomain() } // Assuming you have a toDomain() extension
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
        // According to ML Kit docs, BarcodeScanner instances are thread-safe
        // and it's recommended to call close() when they are no longer needed.
        // If MlKitBarcodeScanner is a @Singleton, this release() might be called
        // when the app shuts down (if called from ViewModel's onCleared).
        try {
            mlKitScanner.close()
        } catch (e: Exception) {
            // Log error or handle, e.g. if called multiple times though close() should be idempotent
        }
    }

    // Make sure you have your toDomain() extension function accessible here
    // e.g., in the same file or imported
    private fun Barcode.toDomain(): BarcodeResult? {
        // Your mapping logic from com.google.mlkit.vision.barcode.common.Barcode
        // to your com.amehran.scanner.domain.model.BarcodeResult
        // This is just a placeholder structure
        return BarcodeResult(
            rawValue = this.rawValue ?: return null,
            format = BarcodeFormat.fromMlKitFormat(this.format),
            type = BarcodeType.fromMlKitType(this.valueType),
            displayValue = this.displayValue,
        )
    }
}