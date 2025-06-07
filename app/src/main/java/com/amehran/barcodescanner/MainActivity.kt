package com.amehran.barcodescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.amehran.barcodescanner.ui.theme.BarcodeScannerSDKTheme
import com.amehran.barcodescanner.ui.screens.BarcodeScannerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Good for modern UI
        setContent {
            BarcodeScannerSDKTheme { // Your app's theme
                // REMOVE Scaffold and Greeting here if BarcodeScannerScreen has its own Scaffold
                BarcodeScannerScreen() // Display your actual scanner screen
            }
        }
    }
}

