import java.text.DateFormat
import java.text.SimpleDateFormat
import com.welie.blessed.BluetoothBytesParser
import java.io.Serializable
import java.util.*

class TemperatureMeasurement(byteArray: ByteArray) {
    val unit: TemperatureUnit
    var temperatureValue: Float
    var timestamp: Date
    var type: TemperatureType? = null

    override fun toString(): String {
        val df: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
        val formattedTimestamp: String
        formattedTimestamp = if (timestamp != null) {
            df.format(timestamp)
        } else {
            "null"
        }
        return String.format(
            Locale.ENGLISH,
            "%.1f %s (%s), at (%s)",
            temperatureValue,
            if (unit == TemperatureUnit.Celsius) "celcius" else "fahrenheit",
            type,
            formattedTimestamp
        )
    }

    init {
        val parser = BluetoothBytesParser(byteArray)
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        unit = if (flags and 0x01 > 0) TemperatureUnit.Fahrenheit else TemperatureUnit.Celsius
        val timestampPresent = flags and 0x02 > 0
        val typePresent = flags and 0x04 > 0

        temperatureValue = parser.getFloatValue(BluetoothBytesParser.FORMAT_FLOAT)

        timestamp = if (timestampPresent) {
            parser.dateTime
        } else {
            Calendar.getInstance().time
        }

        if (typePresent) {
            val typeValue = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
            type = TemperatureType.fromValue(typeValue)
        }
    }
}