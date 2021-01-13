import com.welie.blessed.BluetoothBytesParser

class PulseOximeterContinuousMeasurement(value: ByteArray?) {
    val spO2: Int
    val pulseRate: Int
    var spO2Fast = 0
    var pulseRateFast = 0
    var spO2Slow = 0
    var pulseRateSlow = 0
    var pulseAmplitudeIndex = 0f
    var measurementStatus = 0
    var sensorStatus = 0
    override fun toString(): String {
        return if (spO2 == 2047 || pulseRate == 2047) {
            "invalid measurement"
        } else String.format("SpO2 %d%%, Pulse %d bpm, PAI %.1f", spO2, pulseRate, pulseAmplitudeIndex)
    }

    init {
        val parser = BluetoothBytesParser(value)
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        val spo2FastPresent = flags and 0x01 > 0
        val spo2SlowPresent = flags and 0x02 > 0
        val measurementStatusPresent = flags and 0x04 > 0
        val sensorStatusPresent = flags and 0x08 > 0
        val pulseAmplitudeIndexPresent = flags and 0x10 > 0
        spO2 = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT).toInt()
        pulseRate = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT).toInt()
        if (spo2FastPresent) {
            spO2Fast = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT).toInt()
            pulseRateFast = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT).toInt()
        }
        if (spo2SlowPresent) {
            spO2Slow = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT).toInt()
            pulseRateSlow = parser.getFloatValue(BluetoothBytesParser.FORMAT_SFLOAT).toInt()
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