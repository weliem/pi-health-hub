package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_FLOAT
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.ObservationType
import com.welie.healthhub.Observation
import com.welie.healthhub.gatt.TemperatureType.Unknown
import com.welie.healthhub.gatt.ObservationUnit.Celsius
import com.welie.healthhub.gatt.ObservationUnit.Fahrenheit
import com.welie.healthhub.gatt.TemperatureType.*
import java.util.*

data class TemperatureMeasurement(
    val temperatureValue: Float,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val type: TemperatureType
) {

    fun asObservation(peripheral: BluetoothPeripheral): Observation {
        val now = Calendar.getInstance().time
        return Observation(temperatureValue, type.asObservationType(), unit, timestamp, null, now, peripheral.address)
    }

    private fun TemperatureType.asObservationType(): ObservationType {
        return when(this) {
            Armpit -> ObservationType.ArmpitTemperature
            Ear -> ObservationType.EarTemperature
            Tympanum -> ObservationType.TympanicTemperature
            else -> ObservationType.Temperature
        }
    }

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