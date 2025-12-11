package com.amehran.scanner.data.mapper

import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.domain.model.BarcodeType
import com.google.mlkit.vision.barcode.common.Barcode as MlKitBarcode

/**
 * Maps an ML Kit [MlKitBarcode] to the domain-specific [BarcodeResult].
 *
 * This is internal to the scanner module, as the domain should not be aware of ML Kit specifics.
 * Returns null if the essential [MlKitBarcode.getRawValue] is null.
 */
internal fun MlKitBarcode.toDomain(): BarcodeResult? {
    return BarcodeResult(
        rawValue = this.rawValue ?: return null,
        format = BarcodeFormat.fromMlKitFormat(this.format),
        type = BarcodeType.fromMlKitType(this.valueType),
        displayValue = this.displayValue,
    )
}
