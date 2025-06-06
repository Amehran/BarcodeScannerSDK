package com.amehran.scanner.di

import com.amehran.scanner.data.internal.MlKitBarcodeScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class) // Or appropriate scope
object ScannerModule { // Can be object or class

    @Provides
    fun provideMlKitBarcodeScanner(): MlKitBarcodeScanner {
        return MlKitBarcodeScanner() // You manually create it here
    }
}