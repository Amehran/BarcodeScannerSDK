package com.amehran.scanner.di

import com.amehran.scanner.domain.BarcodeScanner
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SdkEntryPoint {
    fun getBarcodeScannerService(): BarcodeScanner
}