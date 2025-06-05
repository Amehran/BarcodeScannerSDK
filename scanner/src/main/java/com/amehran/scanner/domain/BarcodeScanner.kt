package com.amehran.scanner.domain

import android.graphics.Bitmap
import com.amehran.scanner.domain.model.BarcodeResult
import kotlinx.coroutines.flow.Flow

interface BarcodeScanner {
    /**
     * Processes a given image (represented as a Bitmap) for barcodes.
     *
     * @param imageBitmap The image to scan.
     * @return A Flow emitting a list of BarcodeResult objects found in the image.
     *         The list will be empty if no barcodes are detected.
     *         The Flow can also emit an exception if an error occurs during processing.
     */
    fun processImage(imageBitmap: Bitmap): Flow<List<BarcodeResult>>

    /**
     * Optional: Call this to release any resources held by the scanner implementation
     * when it's no longer needed. This is particularly important if the underlying
     * scanning library requires explicit resource cleanup.
     * The default implementation is empty for convenience if no cleanup is needed.
     */
    fun release() {
        // Default empty implementation
    }
}