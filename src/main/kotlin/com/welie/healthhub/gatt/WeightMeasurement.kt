package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.*
import com.welie.healthhub.observations.ObservationLocation.*
import com.welie.healthhub.observations.ObservationUnit.*
import com.welie.healthhub.sensorType
import java.util.*
import kotlin.math.round

data class WeightMeasurement(
    val weight: Float,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val userID: Int?,
    val bmi: Float?,
    val heightInMetersOrInches: Float?,
    val createdAt: Date = Calendar.getInstance().time
) {
    fun asObservationList(peripheral: BluetoothPeripheral): List<Observation> {
        val systemInfo = requireNotNull(SystemInfoStore.get(peripheral.address))

        val observations = ArrayList<Observation>()
        if (weight in 0.0f..600.0f) {
            observations.add(
                Observation(
                    value = weight,
                    unit = unit,
                    subject = ObservationSubject.Body,
                    quantityType = QuantityType.Mass,
                    timestamp = timestamp,
                    location = Foot,
                    userId = userID,
                    sensorType = peripheral.sensorType(),
                    receivedTimestamp = createdAt,
                    systemInfo = systemInfo
                )
            )
        }
        bmi?.let {
            if (it in 0.0f..50.0f) {
                observations.add(
                    Observation(
                        value = it,
                        unit = KgM2,
                        subject = ObservationSubject.Body,
                        quantityType = QuantityType.LengthMassRatio,
                        timestamp = timestamp,
                        location = Foot,
                        userId = userID,
                        sensorType = peripheral.sensorType(),
                        receivedTimestamp = createdAt,
                        systemInfo = systemInfo
                    )
                )
            }
        }
        heightInMetersOrInches?.let {
            if (it in 0.0f..250.0f) {
                observations.add(
                    Observation(
                        value = it,
                        unit = if (unit == Kilograms) Meters else Inches,
                        subject = ObservationSubject.Body,
                        quantityType = QuantityType.Length,
                        timestamp = timestamp,
                        location = Unknown,
                        userId = userID,
                        sensorType = peripheral.sensorType(),
                        receivedTimestamp = createdAt,
                        systemInfo = systemInfo
                    )
                )
            }
        }
        return observations
    }

    companion object {
        fun fromBytes(value: ByteArray): WeightMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val unit = if (flags and 0x01 > 0) Pounds else Kilograms
            val timestampPresent = flags and 0x02 > 0
            val userIDPresent = flags and 0x04 > 0
            val bmiAndHeightPresent = flags and 0x08 > 0

            val weightMultiplier = if (unit == Kilograms) 0.005f else 0.01f
            val weight = parser.getIntValue(FORMAT_UINT16) * weightMultiplier
            val timestamp = if (timestampPresent) parser.dateTime else null
            val userID = if (userIDPresent) parser.getIntValue(FORMAT_UINT8) else null
            val bmi = if (bmiAndHeightPresent) parser.getIntValue(FORMAT_UINT16) * 0.1f else null
            val heightMultiplier = if (unit == Kilograms) 0.001f else 0.1f
            val height = if (bmiAndHeightPresent) parser.getIntValue(FORMAT_UINT16) * heightMultiplier else null

            return WeightMeasurement(
                weight = round(weight * 100) / 100,
                unit = unit,
                timestamp = timestamp,
                userID = userID,
                bmi = bmi,
                heightInMetersOrInches = height
            )
        }
    }
}