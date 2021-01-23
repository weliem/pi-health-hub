package com.welie.healthhub

import com.welie.blessed.BluetoothBytesParser
import java.util.*

class BloodPressureMeasurement(value: ByteArray) {
    val userID: Int
    val systolic: Float
    val diastolic: Float
    val meanArterialPressure: Float
    val timestamp: Date
    val isMMHG: Boolean
    val pulseRate: Float

    override fun toString(): String {
        return String.format(
            Locale.ENGLISH,
            "%.0f/%.0f %s, MAP %.0f, %.0f bpm, user %d at (%s)",
            systolic,
            diastolic,
            if (isMMHG) "mmHg" else "kPa",
            meanArterialPressure,
            pulseRate,
            userID,
            timestamp
        )
    }

    init {
        val parser = BluetoothBytesParser(value)

        // Parse the flags
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        isMMHG = flags and 0x01 <= 0
        val timestampPresent = flags and 0x02 > 0
        val pulseRatePresent = flags and 0x04 > 0
        val userIdPresent = flags and 0x08 > 0

        // Get systolic, diastolic and mean arterial pressure
        systolic = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)
        diastolic = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)
        meanArterialPressure = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)

        // Read timestamp
        timestamp = if (timestampPresent) {
            parser.dateTime
        } else {
            Calendar.getInstance().time
        }

        // Read pulse rate
        pulseRate = if (pulseRatePresent) {
            parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)
        } else {
            0.0f
        }

        // Read userId
        userID = if (userIdPresent) {
            parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        } else {
            0
        }
    }
}