package com.amehran.barcodescanner.di

import android.content.Context
import com.amehran.scanner.api.BarcodeSDK
import com.amehran.scanner.domain.BarcodeScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppSdkModule {
    @Provides
    @Singleton
    fun provideMySdkBarcodeScanner(
        @ApplicationContext applicationContext: Context
    ): BarcodeScanner { // Return the SDK's interface
        return BarcodeSDK.initialize(applicationContext)
    }
}