package com.amehran.scanner.domain.model

import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode

enum class BarcodeType {
    CONTACT_INFO,
    EMAIL,
    ISBN,
    PHONE,
    PRODUCT,
    SMS,
    TEXT,
    URL,
    WIFI,
    GEO,
    CALENDAR_EVENT,
    DRIVER_LICENSE,
    UNKNOWN;

    companion object {
        fun fromMlKitType(mlKitValueType: Int): BarcodeType {
            return when (mlKitValueType) {
                MlKitBarcode.TYPE_CONTACT_INFO -> CONTACT_INFO
                MlKitBarcode.TYPE_EMAIL -> EMAIL
                MlKitBarcode.TYPE_ISBN -> ISBN
                MlKitBarcode.TYPE_PHONE -> PHONE
                MlKitBarcode.TYPE_PRODUCT -> PRODUCT
                MlKitBarcode.TYPE_SMS -> SMS
                MlKitBarcode.TYPE_TEXT -> TEXT
                MlKitBarcode.TYPE_URL -> URL
                MlKitBarcode.TYPE_WIFI -> WIFI
                MlKitBarcode.TYPE_GEO -> GEO
                MlKitBarcode.TYPE_CALENDAR_EVENT -> CALENDAR_EVENT
                MlKitBarcode.TYPE_DRIVER_LICENSE -> DRIVER_LICENSE
                else -> UNKNOWN
            }
        }
    }
}