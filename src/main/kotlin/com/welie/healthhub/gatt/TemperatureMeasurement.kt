package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_FLOAT
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.Observation
import com.welie.healthhub.gatt.TemperatureType.Unknown
import com.welie.healthhub.observations.ObservationUnit.Celsius
import com.welie.healthhub.observations.ObservationUnit.Fahrenheit
import com.welie.healthhub.gatt.TemperatureType.*
import com.welie.healthhub.isPhilipsThermometer
import com.welie.healthhub.observations.ObservationLocation
import com.welie.healthhub.observations.ObservationType.*
import com.welie.healthhub.observations.ObservationUnit
import com.welie.healthhub.sensorType
import java.util.*

data class TemperatureMeasurement(
    val temperatureValue: Float,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val type: TemperatureType,
    val createdAt: Date = Calendar.getInstance().time
) {
    fun asObservationList(peripheral: BluetoothPeripheral): List<Observation> {
        var finalLocation = type.asObservationLocation()
        var finalType = if (type != Unknown) BodyTemperature else Temperature
        if (peripheral.isPhilipsThermometer()) {
            finalType = BodyTemperature
            finalLocation = ObservationLocation.Ear
        }

        return if (temperatureValue in -200.0f..200.0f) {
            listOf(
                Observation(
                    value = temperatureValue,
                    type = finalType,
                    unit = unit,
                    timestamp = timestamp,
                    location = finalLocation,
                    sensorType = peripheral.sensorType(),
                    receivedTimestamp = createdAt,
                    systemId = peripheral.address
                )
            )
        } else emptyList()
    }

    private fun TemperatureType.asObservationLocation(): ObservationLocation {
        return when (this) {
            Armpit -> ObservationLocation.Armpit
            Ear -> ObservationLocation.Ear
            Tympanum -> ObservationLocation.Tympanum
            Unknown -> ObservationLocation.Unknown
            Body -> ObservationLocation.Body
            Finger -> ObservationLocation.Finger
            GastroIntestinalTract -> ObservationLocation.GastroIntestinalTract
            Mouth -> ObservationLocation.Mouth
            Rectum -> ObservationLocation.Rectum
            Toe -> ObservationLocation.Toe
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