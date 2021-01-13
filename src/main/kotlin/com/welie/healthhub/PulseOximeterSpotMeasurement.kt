package com.welie.healthhub

import java.text.DateFormat
import java.text.SimpleDateFormat
import com.welie.blessed.BluetoothBytesParser
import java.util.*

class PulseOximeterSpotMeasurement(value: ByteArray) {
    val spO2: Float
    val pulseRate: Float
    var pulseAmplitudeIndex = 0f
    val isDeviceClockSet: Boolean
    var timestamp: Date
    var measurementStatus = 0
    var sensorStatus = 0
    override fun toString(): String {
        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val formattedTimestamp = df.format(timestamp)
        return String.format(
            "SpO2 %.0f%% HR: %.0f PAI: %.1f (%s)",
            spO2,
            pulseRate,
            pulseAmplitudeIndex,
            formattedTimestamp
        )
    }

    init {
        val parser = BluetoothBytesParser(value)
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        val timestampPresent = flags and 0x01 > 0
        val measurementStatusPresent = flags and 0x02 > 0
        val sensorStatusPresent = flags and 0x04 > 0
        val pulseAmplitudeIndexPresent = flags and 0x08 > 0
        isDeviceClockSet = flags and 0x10 == 0

        spO2 = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)
        pulseRate = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)

        if (timestampPresent) {
            var timestamp = parser.dateTime
            this.timestamp = timestamp
        } else {
            timestamp = Calendar.getInstance().time
        }
        if (measurementStatusPresent) {
            measurementStatus = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
        }
        if (sensorStatusPresent) {
            sensorStatus = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
            val reservedByte = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        }
        if (pulseAmplitudeIndexPresent) {
            pulseAmplitudeIndex = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT)
        }
    }
}