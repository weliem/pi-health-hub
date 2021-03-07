package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_FLOAT
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.ObservationUnit.Celsius
import com.welie.healthhub.observations.ObservationUnit.Fahrenheit
import com.welie.healthhub.gatt.TemperatureType.*
import com.welie.healthhub.isPhilipsThermometer
import com.welie.healthhub.observations.*
import com.welie.healthhub.sensorType
import java.util.*
import kotlin.math.abs

data class TemperatureMeasurement(
    val temperatureValue: Float,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val type: TemperatureType,
    val createdAt: Date = Calendar.getInstance().time
) {
    fun asObservationList(peripheral: BluetoothPeripheral): List<Observation> {
        val systemInfo = requireNotNull(SystemInfoStore.get(peripheral.address))
        var finalLocation = type.asObservationLocation()
        var finalSubject = if (type != Unknown) ObservationSubject.Body else ObservationSubject.Unknown
        var finalTimestamp = timestamp

        // Peripheral specific corrections
        if (peripheral.isPhilipsThermometer()) {
            finalSubject = ObservationSubject.Body
            finalLocation = ObservationLocation.Ear

            // Correct timestamp if needed
            systemInfo.dateTime?.let {
                val nowInMiliseconds = Calendar.getInstance().time.time
                val intervalWithNow = nowInMiliseconds - it.time
                if (abs(intervalWithNow) > 10000L) {
                    // Current time is wrong so apply correction)
                    var interval = (it.time - timestamp!!.time)
                    finalTimestamp = Date(nowInMiliseconds - interval)
                }
            }
        }

        return if (temperatureValue in -200.0f..200.0f) {
            listOf(
                Observation(
                    value = temperatureValue,
                    unit = unit,
                    subject = finalSubject,
                    quantityType = QuantityType.Temperature,
                    timestamp = timestamp,
                    location = finalLocation,
                    sensorType = peripheral.sensorType(),
                    receivedTimestamp = createdAt,
                    systemInfo = systemInfo
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