package com.welie.healthhub

import java.text.DateFormat
import java.text.SimpleDateFormat
import com.welie.blessed.BluetoothBytesParser
import java.util.*

class WeightMeasurement(byteArray: ByteArray) {
    val weight: Float
    val unit: WeightUnit
    val timestamp: Date
    var userID: Int? = null
    var BMI: Int? = null
    var height: Int? = null

    override fun toString(): String {
        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val formattedTimestamp = if (timestamp != null) df.format(timestamp) else "null"

        return String.format(
            "%.1f %s, user %d, BMI %d, height %d at (%s)",
            weight,
            if (unit == WeightUnit.Kilograms) "kg" else "lb",
            userID,
            BMI,
            height,
            formattedTimestamp
        )
    }

    init {
        val parser = BluetoothBytesParser(byteArray)
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        unit = if (flags and 0x01 > 0) WeightUnit.Pounds else WeightUnit.Kilograms
        val timestampPresent = flags and 0x02 > 0
        val userIDPresent = flags and 0x04 > 0
        val bmiAndHeightPresent = flags and 0x08 > 0

        val weightMultiplier = if (unit == WeightUnit.Kilograms) 0.005f else 0.01f
        weight = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16) * weightMultiplier

        timestamp = if (timestampPresent) {
            parser.dateTime
        } else {
            Calendar.getInstance().time
        }

        if (userIDPresent) {
            userID = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        }

        if (bmiAndHeightPresent) {
            BMI = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
            height = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
        }
    }
}