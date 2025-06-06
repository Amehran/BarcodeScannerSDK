package com.amehran.scanner.domain.model

import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode

enum class BarcodeFormat {
    AZTEC,
    CODABAR,
    CODE_39,
    CODE_93,
    CODE_128,
    DATA_MATRIX,
    EAN_8,
    EAN_13,
    ITF,
    PDF_417,
    QR_CODE,
    UPC_A,
    UPC_E,
    UNKNOWN; // Good to have an unknown/default type

    companion object {
        fun fromMlKitFormat(mlKitFormat: Int): BarcodeFormat {
            return when (mlKitFormat) {
                MlKitBarcode.FORMAT_AZTEC -> AZTEC
                MlKitBarcode.FORMAT_CODABAR -> CODABAR
                MlKitBarcode.FORMAT_CODE_39 -> CODE_39
                MlKitBarcode.FORMAT_CODE_93 -> CODE_93
                MlKitBarcode.FORMAT_CODE_128 -> CODE_128
                MlKitBarcode.FORMAT_DATA_MATRIX -> DATA_MATRIX
                MlKitBarcode.FORMAT_EAN_8 -> EAN_8
                MlKitBarcode.FORMAT_EAN_13 -> EAN_13
                MlKitBarcode.FORMAT_ITF -> ITF
                MlKitBarcode.FORMAT_PDF417 -> PDF_417
                MlKitBarcode.FORMAT_QR_CODE -> QR_CODE
                MlKitBarcode.FORMAT_UPC_A -> UPC_A
                MlKitBarcode.FORMAT_UPC_E -> UPC_E
                // Add any other formats you explicitly support or care about
                else -> UNKNOWN // Default for formats not handled or new ones
            }
        }
    }
}