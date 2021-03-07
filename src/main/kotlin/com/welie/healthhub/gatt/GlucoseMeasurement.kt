package com.welie.healthhub.gatt

import java.util.*
import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.*
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.*
import com.welie.healthhub.observations.ObservationLocation.Finger
import com.welie.healthhub.observations.ObservationUnit.MiligramPerDeciliter
import com.welie.healthhub.observations.ObservationUnit.MmolPerLiter
import com.welie.healthhub.sensorType

data class GlucoseMeasurement(
    val value: Float?,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val sequenceNumber: Int,
    val contextWillFollow: Boolean,
    val createdAt: Date = Calendar.getInstance().time
) {
    fun asObservationList(peripheral: BluetoothPeripheral): List<Observation> {
        if (value == null) return emptyList()
        val systemInfo = requireNotNull(SystemInfoStore.get(peripheral.address))

        return listOf(
            Observation(
                value = value,
                unit = unit,
                subject = ObservationSubject.Glucose,
                quantityType = QuantityType.Concentration,
                volumeOf = VolumeTypes.ArterialBlood,
                timestamp = timestamp,
                location = Finger,
                sensorType = peripheral.sensorType(),
                receivedTimestamp = createdAt,
                systemInfo = systemInfo
            )
        )
    }

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