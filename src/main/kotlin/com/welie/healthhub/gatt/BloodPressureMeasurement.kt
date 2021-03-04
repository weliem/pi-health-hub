package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.*
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.measurementLocation
import com.welie.healthhub.observations.Observation
import com.welie.healthhub.observations.ObservationType.*
import com.welie.healthhub.observations.ObservationUnit
import com.welie.healthhub.observations.ObservationUnit.BeatsPerMinute
import com.welie.healthhub.sensorType
import java.util.*

data class BloodPressureMeasurement(
    val systolic: Float,
    val diastolic: Float,
    val meanArterialPressure: Float,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val pulseRate: Float?,
    val userID: Int?,
    val measurementStatus: BloodPressureMeasurementStatus?,
    val createdAt: Date = Calendar.getInstance().time
) {
    fun asObservationList(peripheral: BluetoothPeripheral): List<Observation> {
        measurementStatus?.let {
            if (measurementStatus.isBodyMovementDetected ||
                measurementStatus.isImproperMeasurementPosition ||
                measurementStatus.isCuffTooLoose
            )
                return emptyList()
        }

        val location = peripheral.measurementLocation()
        val systemId = peripheral.address

        val observations = ArrayList<Observation>()
        observations.add(
            Observation(
                value = systolic,
                type = SystolicPressure,
                unit = unit,
                timestamp = timestamp,
                location = location,
                userId = userID,
                sensorType = peripheral.sensorType(),
                receivedTimestamp = createdAt,
                systemId = systemId
            )
        )
        observations.add(
            Observation(
                value = diastolic,
                type = DiastolicPressure,
                unit = unit,
                timestamp = timestamp,
                location = location,
                userId = userID,
                sensorType = peripheral.sensorType(),
                receivedTimestamp = createdAt,
                systemId = systemId
            )
        )
        observations.add(
            Observation(
                value = meanArterialPressure,
                type = MeanArterialPressure,
                unit = unit,
                timestamp = timestamp, location = location,
                userId = userID,
                sensorType = peripheral.sensorType(),
                receivedTimestamp = createdAt,
                systemId = systemId
            )
        )
        pulseRate?.let {
            observations.add(
                Observation(
                    value = pulseRate,
                    type = HeartRate,
                    unit = BeatsPerMinute,
                    timestamp = timestamp,
                    location = location,
                    userId = userID,
                    sensorType = peripheral.sensorType(),
                    receivedTimestamp = createdAt,
                    systemId = systemId
                )
            )
        }
        return observations
    }

    companion object {
        fun fromBytes(value: ByteArray): BloodPressureMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val unit = if (flags and 0x01 > 0) ObservationUnit.MMHG else ObservationUnit.KPA
            val timestampPresent = flags and 0x02 > 0
            val pulseRatePresent = flags and 0x04 > 0
            val userIdPresent = flags and 0x08 > 0
            val measurementStatusPresent = flags and 0x10 > 0

            val systolic = parser.getFloatValue(FORMAT_SFLOAT)
            val diastolic = parser.getFloatValue(FORMAT_SFLOAT)
            val meanArterialPressure = parser.getFloatValue(FORMAT_SFLOAT)
            val timestamp = if (timestampPresent) parser.dateTime else null
            val pulseRate = if (pulseRatePresent) parser.getFloatValue(FORMAT_SFLOAT) else null
            val userID = if (userIdPresent) parser.getIntValue(FORMAT_UINT8) else null
            val status = if (measurementStatusPresent) BloodPressureMeasurementStatus(parser.getIntValue(FORMAT_UINT16)) else null

            return BloodPressureMeasurement(
                systolic = systolic,
                diastolic = diastolic,
                meanArterialPressure = meanArterialPressure,
                unit = unit,
                timestamp = timestamp,
                pulseRate = pulseRate,
                userID = userID,
                measurementStatus = status
            )
        }
    }
}