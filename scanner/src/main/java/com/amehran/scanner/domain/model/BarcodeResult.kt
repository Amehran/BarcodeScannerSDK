package com.amehran.scanner.domain.model

/**
 * Represents the result of a barcode scan.
 */
data class BarcodeResult(
    val rawValue: String?,
    val format: BarcodeFormat,
    val type: BarcodeType,
    val displayValue: String?
)