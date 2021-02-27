package com.welie.healthhub.gatt

import java.util.*
import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.*
import com.welie.healthhub.gatt.ObservationUnit.MiligramPerDeciliter
import com.welie.healthhub.gatt.ObservationUnit.MmolPerLiter

data class GlucoseMeasurement(
    var value: Float?,
    val unit: ObservationUnit,
    var timestamp: Date?,
    var sequenceNumber: Int,
    var contextWillFollow: Boolean,
    val createdAt: Date = Calendar.getInstance().time
) {
    companion object {
        fun fromBytes(value: ByteArray): GlucoseMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags: Int = parser.getIntValue(FORMAT_UINT8)
            val timeOffsetPresent = flags and 0x01 > 0
            val typeAndLocationPresent = flags and 0x02 > 0
            val unit = if (flags and 0x04 > 0) MmolPerLiter else MiligramPerDeciliter
            val contextWillFollow = flags and 0x10 > 0

            val sequenceNumber = parser.getIntValue(FORMAT_UINT16)
            var timestamp = parser.dateTime
            if (timeOffsetPresent) {
                val timeOffset: Int = parser.getIntValue(FORMAT_SINT16)
                timestamp = Date(timestamp.time + timeOffset * 60000)
            }

            val multiplier = if (unit === MiligramPerDeciliter) 100000 else 1000
            val glucoseValue = if (typeAndLocationPresent) parser.getFloatValue(FORMAT_SFLOAT) * multiplier else null

            return GlucoseMeasurement(
                unit = unit,
                timestamp = timestamp,
                sequenceNumber = sequenceNumber,
                value = glucoseValue,
                contextWillFollow = contextWillFollow
            )
        }
    }
}