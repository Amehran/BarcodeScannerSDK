package com.amehran.scanner.api

import android.content.Context
import android.util.Log
import com.amehran.scanner.di.SdkEntryPoint
import com.amehran.scanner.domain.BarcodeScanner
import dagger.hilt.android.EntryPointAccessors

object BarcodeSDK {
    private var instance: BarcodeScanner? = null

    @JvmStatic
    fun initialize(context: Context): BarcodeScanner {
        Log.d("MyBarcodeSDK", "Initializing...")
        if (instance == null) {
            try {
                val appContext = context.applicationContext
                Log.d("MyBarcodeSDK", "Getting EntryPoint...")
                val entryPoint =
                    EntryPointAccessors.fromApplication(appContext, SdkEntryPoint::class.java)
                Log.d("MyBarcodeSDK", "EntryPoint obtained. Getting BarcodeScannerService...")
                instance = entryPoint.getBarcodeScannerService()
                Log.d("MyBarcodeSDK", "BarcodeScannerService obtained: $instance")
            } catch (e: Exception) {
                Log.e("MyBarcodeSDK", "Error during initialization", e)
                throw e
            }
        }
        Log.d("MyBarcodeSDK", "Initialization complete, returning instance: $instance")
        return instance!!
    }
}