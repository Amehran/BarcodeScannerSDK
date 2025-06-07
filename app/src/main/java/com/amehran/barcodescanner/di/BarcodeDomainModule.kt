package com.amehran.barcodescanner.di

import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
import com.amehran.barcodescanner.domain.ScanBarcodeUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class BarcodeDomainModule {

    @Binds
    abstract fun bindScanBarcodeUseCase(
        scanBarcodeUseCaseImpl: ScanBarcodeUseCaseImpl
    ): ScanBarcodeUseCase
}