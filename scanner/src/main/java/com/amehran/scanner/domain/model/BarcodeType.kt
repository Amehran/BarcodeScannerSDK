package com.amehran.scanner.domain.model

/**
 * Enum representing the type of data encoded in the barcode.
 * These often align with the value types provided by libraries like ML Kit.
 */
enum class BarcodeType {
    TEXT,
    URL,
    WIFI,
    CONTACT_INFO,
    EMAIL,
    PHONE,
    SMS,
    GEO, // Geographic coordinate
    CALENDAR_EVENT,
    PRODUCT,
    ISBN,
    DRIVER_LICENSE, // ML Kit specific, you might or might not need it
    UNKNOWN // For value types not explicitly listed or if detection fails
}