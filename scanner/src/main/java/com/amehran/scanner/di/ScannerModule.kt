package com.amehran.scanner.di

import android.content.Context
import com.amehran.scanner.data.internal.MlKitBarcodeScanner
import com.amehran.scanner.data.mapper.BarcodeMapper
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton
import com.amehran.scanner.domain.BarcodeScanner as DomainBarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode as MlKitCommonBarcode

@Module
@DisableInstallInCheck
object ScannerModule {

    /**
     * Provides the concrete implementation of your SDK's [DomainBarcodeScanner] interface.
     * This is the main service your SDK offers.
     */
    @Provides
    @Singleton
    fun provideDomainBarcodeScannerImplementation(
        @ApplicationContext applicationContext: Context,
        actualMlKitVisionScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
        mapper: BarcodeMapper
    ): DomainBarcodeScanner {
        return MlKitBarcodeScanner(
            applicationContext = applicationContext,
            mlKitScanner = actualMlKitVisionScanner,
            barcodeMapper = mapper
        )
    }

    /**
     * Provides an instance of ML Kit's [com.google.mlkit.vision.barcode.BarcodeScanner].
     * This is an internal dependency for the SDK.
     */
    @Provides
    @Singleton
    fun provideActualMlKitVisionScanner(
        @ApplicationContext context: Context
    ): com.google.mlkit.vision.barcode.BarcodeScanner {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                MlKitCommonBarcode.FORMAT_QR_CODE,
                MlKitCommonBarcode.FORMAT_AZTEC,
                MlKitCommonBarcode.FORMAT_EAN_13,
                MlKitCommonBarcode.FORMAT_EAN_8,
                MlKitCommonBarcode.FORMAT_UPC_A,
                MlKitCommonBarcode.FORMAT_UPC_E,
                MlKitCommonBarcode.FORMAT_CODE_39,
                MlKitCommonBarcode.FORMAT_CODE_93,
                MlKitCommonBarcode.FORMAT_CODE_128,
                MlKitCommonBarcode.FORMAT_CODABAR,
                MlKitCommonBarcode.FORMAT_DATA_MATRIX,
                MlKitCommonBarcode.FORMAT_ITF,
                MlKitCommonBarcode.FORMAT_PDF417
            )
            .build()
        return BarcodeScanning.getClient(options)
    }

    /**
     * Provides an instance of [BarcodeMapper].
     * This is an internal dependency for the SDK, used to map ML Kit models to domain models.
     */
    @Provides

    fun provideBarcodeMapper(): BarcodeMapper {
        return BarcodeMapper()
    }
}