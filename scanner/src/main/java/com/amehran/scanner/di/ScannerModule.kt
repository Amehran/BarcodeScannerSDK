package com.amehran.scanner.di

import com.amehran.scanner.data.internal.MlKitBarcodeScanner
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

@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {

    @Provides
    @Singleton
    fun provideDomainBarcodeScannerImplementation(
        actualMlKitVisionScanner: BarcodeScanner
    ): DomainBarcodeScanner {
        return MlKitBarcodeScanner(actualMlKitVisionScanner)
    }

    @Provides
    @Singleton
    fun provideActualMlKitVisionScanner(): BarcodeScanner {
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
        return BarcodeScanning.getClient(options)
    }
}
