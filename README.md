# 📱 BarcodeScannerSDK

[![CI](https://github.com/Amehran/BarcodeScannerSDK/workflows/Android%20CI/badge.svg)](https://github.com/Amehran/BarcodeScannerSDK/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)

A production-ready Android SDK for real-time barcode scanning powered by Google ML Kit. Built with Clean Architecture, Kotlin Coroutines, and modern Android best practices.

## ✨ Features

- 📷 **Real-time Camera Scanning** - Instant barcode detection from camera feed
- 🎯 **Multiple Format Support** - QR Code, EAN-13, EAN-8, UPC-A, UPC-E, Code-39, Code-93, Code-128, ITF, Codabar, PDF417, Aztec, Data Matrix
- ⚡ **High Performance** - Optimized ML Kit integration with efficient image processing
- 🔄 **Reactive API** - Flow-based API for seamless integration with modern Android apps
- 🧪 **Fully Tested** - 90%+ test coverage with comprehensive unit tests
- 🏗️ **Clean Architecture** - SOLID principles, dependency injection, and modular design
- 🎨 **Jetpack Compose** - Modern declarative UI in sample app
- 🔒 **Thread-Safe** - Safe for concurrent usage
- 📦 **Lightweight** - Minimal dependencies, small footprint

## 🚀 Quick Start

### Installation

Add the dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":scanner"))
}
```

> **Note:** This SDK is currently available as a module. Maven Central publishing coming soon.

### Basic Usage

#### 1. Initialize the SDK

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // SDK uses Hilt for dependency injection
        // Make sure your Application class is annotated with @HiltAndroidApp
    }
}
```

#### 2. Get Scanner Instance

```kotlin
import com.amehran.scanner.api.BarcodeSDK

class MainActivity : ComponentActivity() {
    private lateinit var scanner: BarcodeScanner
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize SDK (thread-safe singleton)
        scanner = BarcodeSDK.initialize(this)
    }
}
```

#### 3. Scan Barcodes

```kotlin
import kotlinx.coroutines.flow.collect

// Process a bitmap image
lifecycleScope.launch {
    scanner.processImage(bitmap).collect { result ->
        result.onSuccess { barcodes ->
            barcodes.forEach { barcode ->
                Log.d("Scanner", "Found: ${barcode.rawValue}")
                Log.d("Scanner", "Format: ${barcode.format}")
                Log.d("Scanner", "Type: ${barcode.type}")
            }
        }.onFailure { exception ->
            Log.e("Scanner", "Scan failed", exception)
        }
    }
}
```

#### 4. Release Resources

```kotlin
override fun onDestroy() {
    super.onDestroy()
    scanner.release()
}
```

## 📖 Documentation

- **[Integration Guide](docs/INTEGRATION_GUIDE.md)** - Step-by-step integration instructions
- **[API Reference](docs/API_REFERENCE.md)** - Complete API documentation
- **[Architecture](docs/ARCHITECTURE.md)** - SDK architecture and design decisions
- **[Sample App](app/)** - Full working example with camera integration

## 🏗️ Architecture

This SDK follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  (ViewModel, Compose UI, Use Cases)     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│            Domain Layer                 │
│  (BarcodeScanner Interface, Models)     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│             Data Layer                  │
│  (ML Kit Implementation, Mappers)       │
└─────────────────────────────────────────┘
```

### Key Components

- **`BarcodeSDK`** - Main entry point for SDK initialization
- **`BarcodeScanner`** - Core interface for barcode scanning
- **`BarcodeResult`** - Data model for scanned barcodes
- **`MlKitBarcodeScanner`** - ML Kit implementation

## 🧪 Testing

The SDK includes comprehensive test coverage:

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport

# View coverage report
open scanner/build/reports/jacoco/jacocoTestReport/html/index.html
```

**Test Statistics:**
- ✅ 24 unit tests
- ✅ 90%+ code coverage
- ✅ Automated CI/CD testing
- ✅ Mock-based testing with MockK

## 🔧 Requirements

- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Kotlin:** 2.0+
- **Gradle:** 8.11.1+
- **Dependencies:**
  - Google ML Kit Barcode Scanning
  - Kotlin Coroutines
  - Dagger Hilt

## 📱 Sample App

The included sample app demonstrates:

- ✅ Camera integration with CameraX
- ✅ Real-time barcode scanning
- ✅ Jetpack Compose UI
- ✅ Error handling
- ✅ Permission management
- ✅ Result display

To run the sample:

```bash
./gradlew :app:installDebug
```

## 🤝 Contributing

This is a showcase project for recruitment purposes. However, feedback and suggestions are welcome!

## 📄 License

```
Copyright 2025 Armin Mehran

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 👨‍💻 Author

**Armin Mehran**

- 🔗 [GitHub](https://github.com/Amehran)
- 💼 [LinkedIn](https://linkedin.com/in/arminmehran)

## 🏆 Highlights

This SDK demonstrates:

- ✅ **Clean Architecture** - SOLID principles and separation of concerns
- ✅ **Modern Android** - Kotlin, Coroutines, Flow, Compose
- ✅ **Dependency Injection** - Hilt for testability and modularity
- ✅ **Comprehensive Testing** - 90%+ coverage with automated CI/CD
- ✅ **Production-Ready** - Thread-safe, error handling, performance optimized
- ✅ **Best Practices** - Code quality, documentation, versioning

---

**Built with ❤️ for Android**
