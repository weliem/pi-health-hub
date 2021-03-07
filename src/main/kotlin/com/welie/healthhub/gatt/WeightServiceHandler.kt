package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.bytes2String
import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.DataCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class WeightServiceHandler : ServiceHandler() {

    override val TAG: String = "WeightServiceHandler"
    override var callback: DataCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>
    ) {
        // A&D peripherals have a DATE TIME characteristic, so write that first
        peripheral.getCharacteristic(SERVICE_UUID, DATE_TIME_CHARACTERISTIC_UUID)?.let {
            writeDateTime(peripheral, it)
            peripheral.readCharacteristic(it)
        }

        peripheral.setNotify(SERVICE_UUID, WSS_MEASUREMENT_CHAR_UUID, true)
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus
    ) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        try {
            when (characteristic.uuid) {
                WSS_MEASUREMENT_CHAR_UUID -> {
                    callback?.onObservationList(WeightMeasurement.fromBytes(value).asObservationList(peripheral))
                    startDisconnectTimer(peripheral)
                }
                DATE_TIME_CHARACTERISTIC_UUID -> {
                    callback?.onPeripheralTime(BluetoothBytesParser(value).dateTime, peripheral.address)
                }
            }
        } catch (exception: Exception) {
            logger.error("could not parse <${bytes2String(value)}> for <${characteristic.uuid}>")
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("0000181D-0000-1000-8000-00805f9b34fb")
        val WSS_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002A9D-0000-1000-8000-00805f9b34fb")
        val DATE_TIME_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb")
    }
}