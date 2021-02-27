package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_SFLOAT
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.Observation
import com.welie.healthhub.ObservationType
import java.util.*

data class BloodPressureMeasurement(
    val systolic: Float,
    val diastolic: Float,
    val meanArterialPressure: Float,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val pulseRate: Float?,
    val userID: Int?
) {

    fun asObservationList(peripheral: BluetoothPeripheral) : List<Observation> {
        val now = Calendar.getInstance().time
        val list = ArrayList<Observation>()
        list.add(Observation(systolic, ObservationType.SystolicCuffPressure, unit, timestamp, userID, now, peripheral.address))
        list.add(Observation(diastolic, ObservationType.DiastolicCuffPressure, unit, timestamp, userID, now, peripheral.address))
        list.add(Observation(meanArterialPressure, ObservationType.MeanArterialCuffPressure, unit, timestamp, userID, now, peripheral.address))
        list.add(Observation(pulseRate, ObservationType.HeartRate, unit, timestamp, userID, now, peripheral.address))
        return list
    }

    companion object {
        fun fromBytes(value: ByteArray): BloodPressureMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val unit = if (flags and 0x01 > 0) ObservationUnit.MMHG else ObservationUnit.KPA
            val timestampPresent = flags and 0x02 > 0
            val pulseRatePresent = flags and 0x04 > 0
            val userIdPresent = flags and 0x08 > 0

            val systolic = parser.getFloatValue(FORMAT_SFLOAT)
            val diastolic = parser.getFloatValue(FORMAT_SFLOAT)
            val meanArterialPressure = parser.getFloatValue(FORMAT_SFLOAT)
            val timestamp = if (timestampPresent) parser.dateTime else null
            val pulseRate = if (pulseRatePresent) parser.getFloatValue(FORMAT_SFLOAT) else null
            val userID = if (userIdPresent) parser.getIntValue(FORMAT_UINT8) else null

            return BloodPressureMeasurement(
                systolic = systolic,
                diastolic = diastolic,
                meanArterialPressure = meanArterialPressure,
                unit = unit,
                timestamp = timestamp,
                pulseRate = pulseRate,
                userID = userID
            )
        }
    }
}