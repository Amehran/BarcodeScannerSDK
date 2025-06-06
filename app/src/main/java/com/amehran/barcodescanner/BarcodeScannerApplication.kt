package com.amehran.barcodescanner

import android.app.Application
import com.amehran.scanner.api.BarcodeSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BarcodeScannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BarcodeSDK.initialize(this)
    }
}