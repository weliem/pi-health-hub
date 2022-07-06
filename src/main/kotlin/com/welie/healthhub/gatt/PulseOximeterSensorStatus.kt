package com.welie.healthhub.gatt

data class PulseOximeterSensorStatus(val sensorStatusFlags: Int) {
    /**
     * Extended Display Update Ongoing
     */
    val isExtendedDisplayUpdateOngoing: Boolean
        get() = sensorStatusFlags and 0x0001 > 0

    /**
     * Equipment Malfunction Detected
     */
    val isEquipmentMalfunctionDetected: Boolean
        get() = sensorStatusFlags and 0x0002 > 0

    /**
     * Signal Processing Irregularity Detected
     */
    val isSignalProcessingIrregularityDetected: Boolean
        get() = sensorStatusFlags and 0x0004 > 0

    /**
     * Inadequate Signal Detected
     */
    val isInadequateSignalDetected: Boolean
        get() = sensorStatusFlags and 0x0008 > 0

    /**
     * Poor Signal Detected
     */
    val isPoorSignalDetected: Boolean
        get() = sensorStatusFlags and 0x0010 > 0

    /**
     * Low Perfusion Detected
     */
    val isLowPerfusionDetected: Boolean
        get() = sensorStatusFlags and 0x0020 > 0

    /**
     * Erratic Signal Detected
     */
    val isErraticSignalDetected: Boolean
        get() = sensorStatusFlags and 0x0040 > 0

    /**
     * Nonpulsatile Signal Detected
     */
    val isNonpulsatileSignalDetected: Boolean
        get() = sensorStatusFlags and 0x0080 > 0

    /**
     * Questionable Pulse Detected
     */
    val isQuestionablePulseDetected: Boolean
        get() = sensorStatusFlags and 0x0100 > 0

    /**
     * Signal Analysis Ongoing
     */
    val isSignalAnalysisOngoing: Boolean
        get() = sensorStatusFlags and 0x0200 > 0

    /**
     * Sensor Interface Detected
     */
    val isSensorInterfaceDetected: Boolean
        get() = sensorStatusFlags and 0x0400 > 0

    /**
     * Sensor Unconnected to User
     */
    val isSensorUnconnectedDetected: Boolean
        get() = sensorStatusFlags and 0x0800 > 0

    /**
     * Unknown Sensor Connected
     */
    val isUnknownSensorConnected: Boolean
        get() = sensorStatusFlags and 0x1000 > 0

    /**
     * Sensor Displaced
     */
    val isSensorDisplaced: Boolean
        get() = sensorStatusFlags and 0x2000 > 0

    /**
     * Sensor Malfunctioning
     */
    val isSensorMalfunctioning: Boolean
        get() = sensorStatusFlags and 0x4000 > 0

    /**
     * Sensor Disconnected
     */
    val isSensorDisconnected: Boolean
        get() = sensorStatusFlags and 0x8000 > 0
}