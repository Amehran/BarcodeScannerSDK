package com.amehran.scanner.di

import android.util.Log
import com.amehran.scanner.data.internal.MlKitBarcodeScanner
import com.amehran.scanner.data.mapper.BarcodeMapper
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.amehran.scanner.domain.BarcodeScanner as DomainBarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode as MlKitCommonBarcode


private const val SCANNER_MODULE_TAG = "ScannerModule"

@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {

    @Provides
    @Singleton
    fun provideDomainBarcodeScannerImplementation(actualMlKitVisionScanner: BarcodeScanner): DomainBarcodeScanner {
        Log.d(SCANNER_MODULE_TAG, "Providing DomainBarcodeScannerImplementation...")
        try {
            val scanner = MlKitBarcodeScanner(actualMlKitVisionScanner)
            Log.d(SCANNER_MODULE_TAG, "DomainBarcodeScannerImplementation provided successfully.")
            return scanner
        } catch (e: Exception) {
            Log.e(SCANNER_MODULE_TAG, "Error in provideDomainBarcodeScannerImplementation", e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideActualMlKitVisionScanner(): BarcodeScanner {
        Log.d(SCANNER_MODULE_TAG, "Providing ActualMlKitVisionScanner...")
        try {
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    MlKitCommonBarcode.FORMAT_QR_CODE,
                    MlKitCommonBarcode.FORMAT_PDF417,
                    MlKitCommonBarcode.FORMAT_EAN_13,
                    MlKitCommonBarcode.FORMAT_EAN_8,
                    MlKitCommonBarcode.FORMAT_UPC_A,
                    MlKitCommonBarcode.FORMAT_UPC_E,
                    MlKitCommonBarcode.FORMAT_CODE_39,
                    MlKitCommonBarcode.FORMAT_CODE_93,
                    MlKitCommonBarcode.FORMAT_CODE_128,
                    MlKitCommonBarcode.FORMAT_ITF,
                    MlKitCommonBarcode.FORMAT_CODABAR,
                    MlKitCommonBarcode.FORMAT_DATA_MATRIX,
                    MlKitCommonBarcode.FORMAT_AZTEC
                )
                .build()
            Log.d(SCANNER_MODULE_TAG, "BarcodeScannerOptions built.")
            val mlKitScanner = BarcodeScanning.getClient(options)
            Log.d(
                SCANNER_MODULE_TAG,
                "ActualMlKitVisionScanner provided successfully: $mlKitScanner"
            )
            return mlKitScanner
        } catch (e: Exception) {
            Log.e(SCANNER_MODULE_TAG, "Error in provideActualMlKitVisionScanner", e)
            throw e
        }
    }

    @Provides
    fun provideBarcodeMapper(): BarcodeMapper {
        Log.d(SCANNER_MODULE_TAG, "Providing BarcodeMapper...")
        try {
            val mapper = BarcodeMapper()
            Log.d(SCANNER_MODULE_TAG, "BarcodeMapper provided successfully.")
            return mapper
        } catch (e: Exception) {
            Log.e(SCANNER_MODULE_TAG, "Error in provideBarcodeMapper", e)
            throw e
        }
    }
}