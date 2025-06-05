package com.amehran.scanner.domain.model

/**
 * Enum representing the format of the scanned barcode.
 * These often align with the formats supported by libraries like ML Kit or ZXing.
 */
enum class BarcodeFormat {
    QR_CODE,
    EAN_13,
    EAN_8,
    UPC_A,
    UPC_E,
    CODE_39,
    CODE_93,
    CODE_128,
    ITF,
    CODABAR,
    DATA_MATRIX,
    PDF_417,
    AZTEC,
    UNKNOWN
}