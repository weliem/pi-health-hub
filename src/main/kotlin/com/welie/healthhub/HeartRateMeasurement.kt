package com.welie.healthhub

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8

data class HeartRateMeasurement(
    val pulse: Int,
    val energyExpended: Int?,
    val rrIntervals: IntArray,
    val sensorContactStatus: SensorContactFeature
) {
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