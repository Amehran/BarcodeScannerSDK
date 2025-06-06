package com.amehran.scanner.di // Or com.amehran.scanner.api

import com.amehran.scanner.domain.BarcodeScanner
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class) // Match the scope of your BarcodeScanner binding
interface SdkEntryPoint {
    fun getBarcodeScannerService(): BarcodeScanner // This should return your SDK's interface
}