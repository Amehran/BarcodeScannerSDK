package com.amehran.barcodescanner.domain

import android.graphics.Bitmap
import android.util.Log
import com.amehran.scanner.domain.BarcodeScanner
import com.amehran.scanner.domain.model.BarcodeResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ScanBarcodeUseCase {
    operator fun invoke(bitmap: Bitmap): Flow<List<BarcodeResult>>
    fun releaseScanner()
}

class ScanBarcodeUseCaseImpl @Inject constructor(
    private val barcodeScannerService: BarcodeScanner
) : ScanBarcodeUseCase {
    init {
        Log.d(
            "MyBarcodeSDK_Tracer",
            "ScanBarcodeUseCaseImpl instance CREATED: ${System.identityHashCode(this)}, using BarcodeScanner: ${
                System.identityHashCode(barcodeScannerService)
            }"
        )
    }

    override operator fun invoke(bitmap: Bitmap): Flow<List<BarcodeResult>> {
        Log.d(
            "MyBarcodeSDK_Tracer",
            "ScanBarcodeUseCase.invoke() CALLED on instance ${System.identityHashCode(this)}. Processing image. Stack trace:",
            Throwable()
        )
        return barcodeScannerService.processImage(bitmap)
            .map { sdkDataList ->
                Log.d(
                    "ScanBarcodeUseCase",
                    "Received from barcodeScannerService. SdkDataList count: ${sdkDataList.size}"
                )
                sdkDataList.map { sdkData ->
                    BarcodeResult(
                        rawValue = sdkData.rawValue,
                        displayValue = sdkData.displayValue,
                        format = sdkData.format,
                        type = sdkData.type
                    )
                }
            }
            .catch { e ->
                Log.e("ScanBarcodeUseCase", "Error in processImage flow", e)
                emit(emptyList())
            }
    }

    override fun releaseScanner() {
        Log.d(
            "MyBarcodeSDK_Tracer",
            "ScanBarcodeUseCase.releaseScanner() CALLED on instance ${System.identityHashCode(this)}. Stack trace:",
            Throwable()
        )
        barcodeScannerService.release()
    }
}