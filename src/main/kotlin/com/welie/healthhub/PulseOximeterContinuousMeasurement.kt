package com.welie.healthhub

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.*

data class PulseOximeterContinuousMeasurement(
    val spO2: Float,
    val pulseRate: Float,
    var spO2Fast: Float?,
    var pulseRateFast: Float?,
    var spO2Slow: Float?,
    var pulseRateSlow: Float?,
    var pulseAmplitudeIndex: Float?,
    var measurementStatus: Int?,
    var sensorStatus: Int?
) {
    companion object {
        fun fromBytes(value: ByteArray): PulseOximeterContinuousMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val spo2FastPresent = flags and 0x01 > 0
            val spo2SlowPresent = flags and 0x02 > 0
            val measurementStatusPresent = flags and 0x04 > 0
            val sensorStatusPresent = flags and 0x08 > 0
            val pulseAmplitudeIndexPresent = flags and 0x10 > 0

            val spO2 = parser.getFloatValue(FORMAT_SFLOAT)
            val pulseRate = parser.getFloatValue(FORMAT_SFLOAT)
            val spO2Fast = if (spo2FastPresent) parser.getFloatValue(FORMAT_SFLOAT) else null
            val pulseRateFast = if (spo2FastPresent) parser.getFloatValue(FORMAT_SFLOAT) else null
            val spO2Slow = if (spo2SlowPresent) parser.getFloatValue(FORMAT_SFLOAT) else null
            val pulseRateSlow = if (spo2SlowPresent) parser.getFloatValue(FORMAT_SFLOAT) else null
            val measurementStatus = if (measurementStatusPresent) parser.getIntValue(FORMAT_UINT16) else null
            val sensorStatus = if (sensorStatusPresent) parser.getIntValue(FORMAT_UINT16) else null
            val reservedByte = if (sensorStatusPresent) parser.getIntValue(FORMAT_UINT8) else null
            val pulseAmplitudeIndex = if (pulseAmplitudeIndexPresent) parser.getFloatValue(FORMAT_SFLOAT) else null

            return PulseOximeterContinuousMeasurement(
                spO2 = spO2,
                pulseRate = pulseRate,
                spO2Fast = spO2Fast,
                pulseRateFast = pulseRateFast,
                spO2Slow = spO2Slow,
                pulseRateSlow = pulseRateSlow,
                measurementStatus = measurementStatus,
                sensorStatus = sensorStatus,
                pulseAmplitudeIndex = pulseAmplitudeIndex
            )
        }
    }
}