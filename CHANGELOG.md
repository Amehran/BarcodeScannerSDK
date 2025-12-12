# Changelog

All notable changes to the BarcodeScannerSDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-12-11

### Added
- Initial release of BarcodeScannerSDK
- Real-time barcode scanning using Google ML Kit
- Support for multiple barcode formats:
  - QR Code
  - EAN-13, EAN-8
  - UPC-A, UPC-E
  - Code-39, Code-93, Code-128
  - ITF, Codabar
  - PDF417, Aztec, Data Matrix
- Flow-based reactive API for barcode processing
- Thread-safe singleton SDK initialization
- Configurable logging for debugging
- Clean Architecture implementation with:
  - Domain layer (interfaces and models)
  - Data layer (ML Kit implementation)
  - Presentation layer (sample app)
- Dependency injection with Hilt
- Comprehensive test suite:
  - 24 unit tests
  - 90%+ code coverage
  - MockK-based testing
  - Coroutine testing support
- CI/CD pipeline with GitHub Actions:
  - Automated unit testing
  - Build caching for performance
  - JaCoCo code coverage reporting
- ProGuard/R8 rules for code minification support
- Sample app demonstrating SDK integration:
  - Camera integration with CameraX
  - Jetpack Compose UI
  - Permission handling
  - Error handling examples

### Documentation
- Comprehensive README with quick start guide
- KDoc comments on public API
- Architecture documentation
- Integration guide
- Sample app examples

### Technical Details
- Min SDK: 24 (Android 7.0)
- Target SDK: 35 (Android 15)
- Kotlin: 2.0
- Gradle: 8.11.1

---

## [Unreleased]

### Planned
- Maven Central publishing
- Additional barcode format support
- Performance optimizations
- Enhanced error handling
- More sample app features

---

## Version History

- **1.0.0** (2025-12-11) - Initial release

---

For more information, see the [README](README.md).
