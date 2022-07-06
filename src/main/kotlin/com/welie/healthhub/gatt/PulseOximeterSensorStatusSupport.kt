package com.welie.healthhub.gatt

data class PulseOximeterSensorStatusSupport(val sensorStatusSupportFlags: Int) {
    /**
     * Extended Display Update Ongoing bit supported
     */
    val isExtendedDisplayUpdateOngoingSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0001 > 0

    /**
     * Equipment Malfunction Detected bit supported
     */
    val isEquipmentMalfunctionDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0002 > 0

    /**
     * Signal Processing Irregularity Detected bit supported
     */
    val isSignalProcessingIrregularityDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0004 > 0

    /**
     * Inadequate Signal Detected bit supported
     */
    val isInadequateSignalDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0008 > 0

    /**
     * Poor Signal Detected bit supported
     */
    val isPoorSignalDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0010 > 0

    /**
     * Low Perfusion Detected bit supported
     */
    val isLowPerfusionDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0020 > 0
    /**
     * Erratic Signal Detected bit supported
     */
    val isErraticSignalDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0040 > 0

    /**
     * Nonpulsatile Signal Detected bit supported
     */
    val isNonpulsatileSignalDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0080 > 0

    /**
     * Questionable Pulse Detected bit supported
     */
    val isQuestionablePulseDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0100 > 0

    /**
     * Signal Analysis Ongoing bit supported
     */
    val isSignalAnalysisOngoingSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0200 > 0

    /**
     * Sensor Interface Detected bit supported
     */
    val isSensorInterfaceDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0400 > 0

    /**
     * Sensor Unconnected to User bit supported
     */
    val isSensorUnconnectedDetectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x0800 > 0

    /**
     * Unknown Sensor Connected bit supported
     */
    val isUnknownSensorConnectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x1000 > 0

    /**
     * Sensor Displaced bit supported
     */
    val isSensorDisplacedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x2000 > 0

    /**
     * Sensor Malfunctioning bit supported
     */
    val isSensorMalfunctioningSupported: Boolean
        get() = sensorStatusSupportFlags and 0x4000 > 0

    /**
     * Sensor Disconnected bit supported
     */
    val isSensorDisconnectedSupported: Boolean
        get() = sensorStatusSupportFlags and 0x8000 > 0
}