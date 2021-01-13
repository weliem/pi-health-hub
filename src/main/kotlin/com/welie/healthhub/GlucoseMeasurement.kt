package com.welie.healthhub

import java.io.Serializable
import java.util.*
import com.welie.blessed.BluetoothBytesParser

class GlucoseMeasurement(byteArray: ByteArray) : Serializable {
    val unit: GlucoseMeasurementUnit
    var timestamp: Date
    var sequenceNumber: Int
    var contextWillFollow: Boolean
    var value = 0f

    override fun toString(): String {
        return String.format(
            Locale.ENGLISH,
            "%.1f %s, at (%s)",
            value,
            if (unit === GlucoseMeasurementUnit.MmolPerLiter) "mmol/L" else "mg/dL",
            timestamp
        )
    }

    init {
        val parser = BluetoothBytesParser(byteArray)

        // Parse flags
        val flags: Int = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        val timeOffsetPresent = flags and 0x01 > 0
        val typeAndLocationPresent = flags and 0x02 > 0
        unit = if (flags and 0x04 > 0) GlucoseMeasurementUnit.MmolPerLiter else GlucoseMeasurementUnit.MiligramPerDeciliter
        contextWillFollow = flags and 0x10 > 0

        // Sequence number is used to match the reading with an optional glucose context
        sequenceNumber = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)

        // Read timestamp
        timestamp = parser.dateTime
        if (timeOffsetPresent) {
            val timeOffset: Int = parser.getIntValue(BluetoothBytesParser.FORMAT_SINT16)
            timestamp = Date(timestamp.time + timeOffset * 60000)
        }
        if (typeAndLocationPresent) {
            val glucoseConcentration: Float = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)
            val multiplier = if (unit === GlucoseMeasurementUnit.MiligramPerDeciliter) 100000 else 1000
            value = glucoseConcentration * multiplier
        }
    }
}