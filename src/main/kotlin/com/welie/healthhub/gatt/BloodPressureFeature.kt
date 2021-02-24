/*
 * Copyright (c) Koninklijke Philips N.V. 2020.
 * All rights reserved.
 */
package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser

data class BloodPressureFeature(
    val isBodyMovementDetectionSupported: Boolean,
    val isCuffFitDetectionSupported: Boolean,
    val isIrregularPulseDetectionSupported: Boolean,
    val isPulseRateRangeDetectionSupported: Boolean,
    val isMeasurementPositionDetectionSupported: Boolean,
    val isMultipleBondSupported: Boolean
) {
    companion object {
        fun fromBytes(value: ByteArray): BloodPressureFeature {
            val flags = BluetoothBytesParser(value).getIntValue(BluetoothBytesParser.FORMAT_UINT8)

            return BloodPressureFeature(
                isBodyMovementDetectionSupported = flags and 0x01 > 0,
                isCuffFitDetectionSupported = flags and 0x02 > 0,
                isIrregularPulseDetectionSupported = flags and 0x04 > 0,
                isPulseRateRangeDetectionSupported = flags and 0x08 > 0,
                isMeasurementPositionDetectionSupported = flags and 0x10 > 0,
                isMultipleBondSupported = flags and 0x20 > 0
            )
        }
    }
}