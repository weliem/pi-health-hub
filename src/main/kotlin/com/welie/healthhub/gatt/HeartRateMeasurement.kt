package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.measurementLocation
import com.welie.healthhub.observations.Observation
import com.welie.healthhub.observations.ObservationLocation
import com.welie.healthhub.observations.ObservationLocation.Unknown
import com.welie.healthhub.observations.ObservationType.HeartRate
import com.welie.healthhub.observations.ObservationUnit.BeatsPerMinute
import com.welie.healthhub.sensorType
import java.util.*
import kotlin.collections.ArrayList

data class HeartRateMeasurement(
    val pulse: Int,
    val energyExpended: Int?,
    val rrIntervals: IntArray,
    val sensorContactStatus: SensorContactFeature,
    val createdAt: Date = Calendar.getInstance().time
) {
    fun asObservationList(peripheral: BluetoothPeripheral): List<Observation> {
        if (sensorContactStatus == SensorContactFeature.SupportedNoContact) return emptyList()

        return if (pulse in 20..250) {
            listOf(Observation(
                value = pulse.toFloat(),
                type = HeartRate,
                unit = BeatsPerMinute,
                timestamp = createdAt,
                location = peripheral.measurementLocation(),
                sensorType = peripheral.sensorType(),
                systemId = peripheral.address,
                receivedTimestamp = createdAt
               ))
        } else emptyList()
    }

    companion object {
        fun fromBytes(value: ByteArray): HeartRateMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val pulse = if (flags and 0x01 == 0) parser.getIntValue(FORMAT_UINT8) else parser.getIntValue(FORMAT_UINT16)
            val sensorContactStatusFlag = flags and 0x06 shr 1
            val energyExpenditurePresent = flags and 0x08 > 0
            val rrIntervalPresent = flags and 0x10 > 0

            val sensorContactStatus = when (sensorContactStatusFlag) {
                0, 1 ->  //Sensor Contact feature is not supported in the current connection
                    SensorContactFeature.NotSupported
                2 ->  //Sensor Contact feature is supported, but contact is not detected
                    SensorContactFeature.SupportedNoContact
                3 ->  //Sensor Contact feature is supported and contact is detected
                    SensorContactFeature.SupportedAndContact
                else -> SensorContactFeature.NotSupported
            }

            val energyExpended = if (energyExpenditurePresent) parser.getIntValue(FORMAT_UINT16) else null

            val rrArray = ArrayList<Int>()
            if (rrIntervalPresent) {
                while (parser.offset < value.size) {
                    val rrInterval = parser.getIntValue(FORMAT_UINT16)
                    rrArray.add((rrInterval.toDouble() / 1024.0 * 1000.0).toInt())
                }
            }

            return HeartRateMeasurement(
                pulse = requireNotNull(pulse),
                energyExpended = energyExpended,
                sensorContactStatus = sensorContactStatus,
                rrIntervals = rrArray.toIntArray()
            )
        }
    }
}