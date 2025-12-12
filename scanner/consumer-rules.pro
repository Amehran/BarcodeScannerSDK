# BarcodeScannerSDK ProGuard Rules
# These rules ensure the SDK works correctly when code minification is enabled

# Keep public API
-keep class com.amehran.scanner.api.** { *; }
-keep class com.amehran.scanner.domain.** { *; }
-keep class com.amehran.scanner.domain.model.** { *; }

# Keep BarcodeSDK public methods and constants
-keepclassmembers class com.amehran.scanner.api.BarcodeSDK {
    public static final java.lang.String VERSION;
    public static final int VERSION_CODE;
    public static boolean loggingEnabled;
    public static *** initialize(android.content.Context);
}

# Keep BarcodeScanner interface
-keep interface com.amehran.scanner.domain.BarcodeScanner {
    *;
}

# Keep data models
-keep class com.amehran.scanner.domain.model.BarcodeResult {
    *;
}
-keep class com.amehran.scanner.domain.model.BarcodeFormat {
    *;
}
-keep class com.amehran.scanner.domain.model.BarcodeType {
    *;
}

# Keep ML Kit classes
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# Keep Hilt generated classes
-keep class **_HiltModules* { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-keep class **_Impl { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# Keep Hilt entry points
-keep class com.amehran.scanner.di.SdkEntryPoint { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.android.EntryPoint class * { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Flow classes
-keep class kotlinx.coroutines.flow.** { *; }
-dontwarn kotlinx.coroutines.flow.**

# Suppress warnings for optional dependencies
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**

# Keep source file names and line numbers for better stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
