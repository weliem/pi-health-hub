import com.welie.blessed.BluetoothBytesParser
import java.util.*

class HeartRateMeasurement(value: ByteArray) {
    val pulse: Int
    
    override fun toString(): String {
        return String.format(Locale.ENGLISH, "Pulse %d bpm", pulse)
    }

    init {
        val parser = BluetoothBytesParser(value)
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        val unit = flags and 0x01

        pulse = if (unit == 0) parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8) else parser.getIntValue(
            BluetoothBytesParser.FORMAT_UINT16
        )
    }
}