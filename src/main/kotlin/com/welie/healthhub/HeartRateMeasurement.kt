package com.welie.healthhub

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8

data class HeartRateMeasurement(
    val pulse: Int
) {
    companion object {
        fun fromBytes(value: ByteArray): HeartRateMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val pulse = if (flags and 0x01 == 0) parser.getIntValue(FORMAT_UINT8) else parser.getIntValue(FORMAT_UINT16)

            return HeartRateMeasurement(pulse = pulse)
        }
    }
}