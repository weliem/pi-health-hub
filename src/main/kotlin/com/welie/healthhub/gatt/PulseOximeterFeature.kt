package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser.*

class PulseOximeterFeature(byteArray: ByteArray) {
    val isMeasurementStatusSupportPresent: Boolean
    val isSensorStatusSupportPresent: Boolean
    val isMeasurementStorageSupported: Boolean
    val isTimestampSupported: Boolean
    val isSpo2prFastSupported: Boolean
    val isSpo2prSlowSupported: Boolean
    val isPulseAmplitudeIndexSupported: Boolean
    val isMultiBondSupported: Boolean
    var measurementStatusSupport: PulseOximeterMeasurementStatusSupport? = null
    var sensorStatusSupport: PulseOximeterSensorStatusSupport? = null

    init {
        val parser = BluetoothBytesParser(byteArray)
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
        requireNotNull(flags)

        isMeasurementStatusSupportPresent = flags and 0x0001 > 0
        isSensorStatusSupportPresent = flags and 0x0002 > 0
        isMeasurementStorageSupported = flags and 0x0004 > 0
        isTimestampSupported = flags and 0x0008 > 0
        isSpo2prFastSupported = flags and 0x0010 > 0
        isSpo2prSlowSupported = flags and 0x0020 > 0
        isPulseAmplitudeIndexSupported = flags and 0x0040 > 0
        isMultiBondSupported = flags and 0x0080 > 0
        if (isMeasurementStatusSupportPresent) {
            measurementStatusSupport = PulseOximeterMeasurementStatusSupport(parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)!!)
        }
        if (isSensorStatusSupportPresent) {
            sensorStatusSupport = PulseOximeterSensorStatusSupport(parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)!!)
        }
    }
}