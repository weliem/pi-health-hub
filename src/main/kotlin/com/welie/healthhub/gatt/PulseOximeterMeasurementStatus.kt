/*
 * Copyright (c) Koninklijke Philips N.V., 2020.
 * All rights reserved.
 */
package com.welie.healthhub.gatt

data class PulseOximeterMeasurementStatus(val measurementStatusFlags: Int) {
    /**
     * Measurement Ongoing
     */
    val isMeasurementOngoing: Boolean
        get() = measurementStatusFlags and 0x0020 > 0

    /**
     * Early Estimated Data
     */
    val isEarlyEstimateData: Boolean
        get() = measurementStatusFlags and 0x0040 > 0

    /**
     * Validated Data
     */
    val isValidatedData: Boolean
        get() = measurementStatusFlags and 0x0080 > 0

    /**
     * Fully Qualified Data
     */
    val isFullyQualifiedData: Boolean
        get() = measurementStatusFlags and 0x0100 > 0

    /**
     * Data from Measurement Storage
     */
    val isDataMeasurementStorage: Boolean
        get() = measurementStatusFlags and 0x0200 > 0

    /**
     * Data for Demonstration
     */
    val isDataDemonstration: Boolean
        get() = measurementStatusFlags and 0x0400 > 0

    /**
     * Data for Testing
     */
    val isDataTesting: Boolean
        get() = measurementStatusFlags and 0x0800 > 0

    /**
     * Calibration Ongoing
     */
    val isCalibrationOngoing: Boolean
        get() = measurementStatusFlags and 0x1000 > 0
    /**
     * Measurement unavailable
     */
    val isMeasurementUnavailable: Boolean
        get() = measurementStatusFlags and 0x2000 > 0

    /**
     * Questionable Measurement Detected
     */
    val isQuestionableMeasurementDetected: Boolean
        get() = measurementStatusFlags and 0x4000 > 0

    /**
     * Invalid Measurement Detected
     */
    val isInvalidMeasurementDetected: Boolean
        get() = measurementStatusFlags and 0x8000 > 0

}