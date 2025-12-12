package com.amehran.scanner.api

import android.content.Context
import android.util.Log
import com.amehran.scanner.di.SdkEntryPoint
import com.amehran.scanner.domain.BarcodeScanner
import dagger.hilt.android.EntryPointAccessors

/**
 * Main entry point for the Barcode Scanner SDK.
 *
 * This SDK provides real-time barcode scanning capabilities using Google ML Kit.
 * It supports multiple barcode formats including QR codes, EAN, UPC, and more.
 *
 * ## Thread Safety
 * This class is thread-safe. Multiple threads can safely call [initialize] concurrently.
 *
 * ## Usage Example
 * ```kotlin
 * // Initialize SDK (typically in Application.onCreate or Activity.onCreate)
 * val scanner = BarcodeSDK.initialize(context)
 *
 * // Process an image
 * lifecycleScope.launch {
 *     scanner.processImage(bitmap).collect { result ->
 *         result.onSuccess { barcodes ->
 *             // Handle successful scan
 *         }.onFailure { exception ->
 *             // Handle error
 *         }
 *     }
 * }
 *
 * // Release resources when done
 * scanner.release()
 * ```
 *
 * @since 1.0.0
 */
object BarcodeSDK {
    
    /**
     * SDK version name.
     * Format: MAJOR.MINOR.PATCH (Semantic Versioning)
     */
    const val VERSION = "1.0.0"
    
    /**
     * SDK version code.
     * Incremented with each release.
     */
    const val VERSION_CODE = 1
    
    /**
     * Enable or disable SDK logging.
     * 
     * When enabled, the SDK will output debug logs to help with troubleshooting.
     * Disable in production for better performance.
     * 
     * Default: false
     */
    @JvmStatic
    var loggingEnabled: Boolean = false
    
    private const val TAG = "BarcodeSDK"
    
    @Volatile
    private var instance: BarcodeScanner? = null
    
    /**
     * Initializes the Barcode Scanner SDK.
     *
     * This method returns a singleton instance of [BarcodeScanner]. Subsequent calls
     * with the same or different contexts will return the same instance.
     *
     * ## Thread Safety
     * This method is thread-safe and can be called from multiple threads concurrently.
     *
     * ## Context Usage
     * The SDK uses the application context internally to prevent memory leaks.
     * You can safely pass an Activity context - it will be converted to application context.
     *
     * @param context Android context (Activity or Application context)
     * @return BarcodeScanner singleton instance
     * @throws IllegalStateException if Hilt dependency injection is not properly initialized
     * @throws IllegalArgumentException if context is null
     *
     * @since 1.0.0
     */
    @JvmStatic
    @Synchronized
    fun initialize(context: Context): BarcodeScanner {
        require(context != null) { "Context cannot be null" }
        
        return instance ?: run {
            log("Initializing BarcodeSDK v$VERSION")
            
            try {
                val appContext = context.applicationContext
                log("Retrieving SDK entry point from Hilt")
                
                val entryPoint = EntryPointAccessors.fromApplication(
                    appContext,
                    SdkEntryPoint::class.java
                )
                
                val scanner = entryPoint.getBarcodeScannerService()
                log("BarcodeSDK initialized successfully")
                
                instance = scanner
                scanner
            } catch (e: IllegalStateException) {
                logError("Failed to initialize SDK: Hilt not configured properly", e)
                throw IllegalStateException(
                    "BarcodeSDK initialization failed. Ensure your Application class is annotated with @HiltAndroidApp",
                    e
                )
            } catch (e: Exception) {
                logError("Unexpected error during SDK initialization", e)
                throw e
            }
        }
    }
    
    /**
     * Internal logging function.
     * Only logs when [loggingEnabled] is true.
     */
    private fun log(message: String) {
        if (loggingEnabled) {
            Log.d(TAG, message)
        }
    }
    
    /**
     * Internal error logging function.
     * Always logs errors regardless of [loggingEnabled] setting.
     */
    private fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}