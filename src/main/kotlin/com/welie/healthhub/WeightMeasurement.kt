package com.welie.healthhub

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import java.util.*
import kotlin.math.round

data class WeightMeasurement(
    val weight: Float,
    val unit: WeightUnit,
    val timestamp: Date?,
    val userID: Int?,
    val bmi: Int?,
    val height: Int?
) {
    companion object {
        fun fromBytes(value: ByteArray): WeightMeasurement {
            val parser = BluetoothBytesParser(value)
            val flags = parser.getIntValue(FORMAT_UINT8)
            val unit = if (flags and 0x01 > 0) WeightUnit.Pounds else WeightUnit.Kilograms
            val timestampPresent = flags and 0x02 > 0
            val userIDPresent = flags and 0x04 > 0
            val bmiAndHeightPresent = flags and 0x08 > 0

            val weightMultiplier = if (unit == WeightUnit.Kilograms) 0.005f else 0.01f
            val weight = round(parser.getIntValue(FORMAT_UINT16) * weightMultiplier * 100) / 100
            val timestamp = if (timestampPresent) parser.dateTime else null
            val userID = if (userIDPresent) parser.getIntValue(FORMAT_UINT8) else null
            val bmi = if (bmiAndHeightPresent) parser.getIntValue(FORMAT_UINT16) else null
            val height = if (bmiAndHeightPresent) parser.getIntValue(FORMAT_UINT16) else null

            return WeightMeasurement(
                weight = weight,
                unit = unit,
                timestamp = timestamp,
                userID = userID,
                bmi = bmi,
                height = height
            )
        }
    }
}