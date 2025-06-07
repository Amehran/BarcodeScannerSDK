package com.amehran.scanner.domain

import android.graphics.Bitmap
import com.amehran.scanner.domain.model.BarcodeResult
import kotlinx.coroutines.flow.Flow

interface BarcodeScanner {
    fun processImage(imageBitmap: Bitmap): Flow<List<BarcodeResult>>
    fun release()
}