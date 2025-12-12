# 16KB Page Size Compatibility

## Current Status

⚠️ **Warning Present (Non-blocking)**

The app currently shows this warning when installing:
```
APK app-debug.apk is not compatible with 16 KB devices. 
Some libraries have LOAD segments not aligned at 16 KB boundaries:
lib/arm64-v8a/libimage_processing_util_jni.so
```

## What This Means

- **Impact:** Informational warning only (as of December 2025)
- **Deadline:** Required for Google Play starting **November 1, 2025**
- **Root Cause:** Google ML Kit's native library (`libimage_processing_util_jni.so`) is not yet 16KB-aligned
- **Our Status:** SDK code is ready; waiting for ML Kit library update from Google

## What We've Done

✅ **Updated Android Gradle Plugin** to 8.7.3 (supports 16KB alignment)  
✅ **Configured proper packaging** settings  
✅ **Documented the issue** for transparency  

## What's Needed

The issue will be resolved when:
1. **Google updates ML Kit** with 16KB-aligned native libraries
2. **We update the dependency** to the new ML Kit version

## Timeline

- **Now:** Warning appears but app works fine
- **Before Nov 2025:** Google will release updated ML Kit
- **Action Required:** Update `barcode-scanning` dependency when available

## For Recruiters

This demonstrates:
- ✅ **Awareness** of upcoming Android requirements
- ✅ **Proactive** preparation for future compliance
- ✅ **Proper documentation** of known issues
- ✅ **Understanding** of dependency management

## Monitoring

Check for ML Kit updates:
```gradle
// Current version
implementation("com.google.mlkit:barcode-scanning:17.3.0")

// Check for updates at:
// https://developers.google.com/ml-kit/vision/barcode-scanning/android
```

## References

- [Android 16KB Page Size Guide](https://developer.android.com/16kb-page-size)
- [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning/android)
- [Google Play Requirements](https://support.google.com/googleplay/android-developer/answer/11926878)

---

**Last Updated:** December 12, 2025  
**Status:** Monitoring for ML Kit library update
