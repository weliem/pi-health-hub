package com.welie.healthhub.gatt

class PulseOximeterMeasurementStatusSupport(val measurementStatusSupportFlags: Int) {
    /**
     * Measurement Ongoing bit supported
     */
    val isMeasurementOngoingSupported: Boolean
        get() = measurementStatusSupportFlags and 0x0020 > 0
    /**
     * Early Estimated Data bit supported
     */
    val isEarlyEstimateDataSupported: Boolean
        get() = measurementStatusSupportFlags and 0x0040 > 0

    /**
     * Validated Data bit supported
     */
    val isValidatedDataSupported: Boolean
        get() = measurementStatusSupportFlags and 0x0080 > 0

    /**
     * Fully Qualified Data bit supported
     */
    val isFullyQualifiedDataSupported: Boolean
        get() = measurementStatusSupportFlags and 0x0100 > 0

    /**
     * Data from Measurement Storage bit supported
     */
    val isDataMeasurementStorageSupported: Boolean
        get() = measurementStatusSupportFlags and 0x0200 > 0

    /**
     * Data for Demonstration bit supported
     */
    val isDataDemonstrationSupported: Boolean
        get() = measurementStatusSupportFlags and 0x0400 > 0

    /**
     * Data for Testing bit supported
     */
    val isDataTestingSupported: Boolean
        get() = measurementStatusSupportFlags and 0x0800 > 0

    /**
     * Calibration Ongoing bit supported
     */
    val isCalibrationOngoingSupported: Boolean
        get() = measurementStatusSupportFlags and 0x1000 > 0

    /**
     * Measurement unavailable bit supported
     */
    val isMeasurementUnavailableSupported: Boolean
        get() = measurementStatusSupportFlags and 0x2000 > 0

    /**
     * Questionable Measurement Detected bit supported
     */
    val isQuestionableMeasurementDetectedSupported: Boolean
        get() = measurementStatusSupportFlags and 0x4000 > 0

    /**
     * Invalid Measurement Detected bit supported
     */
    val isInvalidMeasurementDetectedSupported: Boolean
        get() = measurementStatusSupportFlags and 0x8000 > 0
}