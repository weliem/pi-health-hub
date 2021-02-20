package com.welie.healthhub

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_FLOAT
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.healthhub.TemperatureType.Unknown
import com.welie.healthhub.TemperatureUnit.Celsius
import com.welie.healthhub.TemperatureUnit.Fahrenheit
import java.util.*

data class TemperatureMeasurement(
    val temperatureValue: Float,
    val unit: TemperatureUnit,
    val timestamp: Date?,
    val type: TemperatureType
) {
    companion object {
        fun fromBytes(value: ByteArray): TemperatureMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val unit = if (flags and 0x01 > 0) Fahrenheit else Celsius
            val timestampPresent = flags and 0x02 > 0
            val typePresent = flags and 0x04 > 0

            val temperatureValue = parser.getFloatValue(FORMAT_FLOAT)
            val timestamp = if (timestampPresent) parser.dateTime else null
            val type = if (typePresent) TemperatureType.fromValue(parser.getIntValue(FORMAT_UINT8)) else Unknown

            return TemperatureMeasurement(
                unit = unit,
                temperatureValue = temperatureValue,
                timestamp = timestamp,
                type = type
            )
        }
    }
}