package com.amehran.scanner.data.internal // Adjust package if you chose a different location

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.data.mapper.toDomain
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class MlKitBarcodeScanner : BarcodeScanner { // Simple constructor for now
    private val mlKitScanner = BarcodeScanning.getClient() // Or your options

    override fun processImage(imageBitmap: Bitmap): Flow<List<BarcodeResult>> { // <--- SIGNATURE MUST MATCH INTERFACE
        return callbackFlow {
            val image = InputImage.fromBitmap(imageBitmap, 0)
            mlKitScanner.process(image)
                .addOnSuccessListener { mlKitBarcodes ->
                    val domainBarcodes = mlKitBarcodes.mapNotNull { it?.toDomain() } // Use your mapper
                    trySend(domainBarcodes)
                    close() // Close the flow after sending the result
                }
                .addOnFailureListener { exception ->
                    close(exception) // Close the flow with an error
                }
            awaitClose {
                // Optional: any cleanup when the flow is cancelled
            }
        }
    }

    override fun release() {
        // mlKitScanner.close() // If the underlying scanner has a close/release method
    }
}