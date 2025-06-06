package com.amehran.scanner.api

import android.content.Context
import com.amehran.scanner.di.SdkEntryPoint
import com.amehran.scanner.domain.BarcodeScanner
import dagger.hilt.android.EntryPointAccessors

object BarcodeSDK {
    private var instance: BarcodeScanner? = null

    @JvmStatic
    fun initialize(context: Context): BarcodeScanner {
        if (instance == null) {
            val appContext = context.applicationContext
            val entryPoint =
                EntryPointAccessors.fromApplication(appContext, SdkEntryPoint::class.java)
            instance =
                entryPoint.getBarcodeScannerService()
        }
        return instance!!
    }
}