package com.amehran.scanner.data.mapper

import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.domain.model.BarcodeType

// Import the ML Kit Barcode class (or the equivalent for your chosen library)
import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode // Use an alias for clarity

class BarcodeMapper() {
    fun MlKitBarcode.toDomain(): BarcodeResult {
        return BarcodeResult(
            rawValue = this.rawValue,
            format = mapMlKitFormatToDomain(this.format),
            type = mapMlKitTypeToDomain(this.valueType),
            displayValue = this.displayValue
        )
    }

    private fun mapMlKitFormatToDomain(mlKitFormat: Int): BarcodeFormat {
        return when (mlKitFormat) {
            MlKitBarcode.FORMAT_QR_CODE -> BarcodeFormat.QR_CODE
            MlKitBarcode.FORMAT_EAN_13 -> BarcodeFormat.EAN_13
            MlKitBarcode.FORMAT_EAN_8 -> BarcodeFormat.EAN_8
            MlKitBarcode.FORMAT_UPC_A -> BarcodeFormat.UPC_A
            MlKitBarcode.FORMAT_UPC_E -> BarcodeFormat.UPC_E
            MlKitBarcode.FORMAT_CODE_39 -> BarcodeFormat.CODE_39
            MlKitBarcode.FORMAT_CODE_93 -> BarcodeFormat.CODE_93
            MlKitBarcode.FORMAT_CODE_128 -> BarcodeFormat.CODE_128
            MlKitBarcode.FORMAT_ITF -> BarcodeFormat.ITF
            MlKitBarcode.FORMAT_CODABAR -> BarcodeFormat.CODABAR
            MlKitBarcode.FORMAT_DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
            MlKitBarcode.FORMAT_AZTEC -> BarcodeFormat.AZTEC
            else -> BarcodeFormat.UNKNOWN
        }
    }

    private fun mapMlKitTypeToDomain(mlKitType: Int): BarcodeType {
        return when (mlKitType) {
            MlKitBarcode.TYPE_TEXT -> BarcodeType.TEXT
            MlKitBarcode.TYPE_URL -> BarcodeType.URL
            MlKitBarcode.TYPE_WIFI -> BarcodeType.WIFI
            MlKitBarcode.TYPE_CONTACT_INFO -> BarcodeType.CONTACT_INFO
            MlKitBarcode.TYPE_EMAIL -> BarcodeType.EMAIL
            MlKitBarcode.TYPE_PHONE -> BarcodeType.PHONE
            MlKitBarcode.TYPE_SMS -> BarcodeType.SMS
            MlKitBarcode.TYPE_GEO -> BarcodeType.GEO
            MlKitBarcode.TYPE_CALENDAR_EVENT -> BarcodeType.CALENDAR_EVENT
            MlKitBarcode.TYPE_PRODUCT -> BarcodeType.PRODUCT
            MlKitBarcode.TYPE_ISBN -> BarcodeType.ISBN
            MlKitBarcode.TYPE_DRIVER_LICENSE -> BarcodeType.DRIVER_LICENSE
            else -> BarcodeType.UNKNOWN
        }
    }
}