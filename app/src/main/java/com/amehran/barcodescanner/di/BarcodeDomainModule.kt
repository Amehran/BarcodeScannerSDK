package com.amehran.barcodescanner.di // Or your DI package

import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
import com.amehran.barcodescanner.domain.ScanBarcodeUseCaseImpl // Your implementation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class) // Scoped to ViewModels
abstract class BarcodeDomainModule {

    @Binds
    abstract fun bindScanBarcodeUseCase(
        scanBarcodeUseCaseImpl: ScanBarcodeUseCaseImpl
    ): ScanBarcodeUseCase
}