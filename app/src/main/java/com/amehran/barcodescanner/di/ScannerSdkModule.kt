package com.amehran.barcodescanner.di // Or your actual package

import com.amehran.scanner.data.internal.MlKitBarcodeScanner
import com.amehran.scanner.domain.BarcodeScanner


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerModule {

    @Binds
    @Singleton
    abstract fun bindBarcodeScanner(
        // Parameter type: Your concrete implementation
        mlKitImplementation: MlKitBarcodeScanner
    ): BarcodeScanner // Return type: YOUR DOMAIN INTERFACE
}