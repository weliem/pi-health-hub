package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.DataCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class TemperatureServiceHandler : ServiceHandler() {

    override val TAG: String = "TemperatureServiceHandler"
    override var callback: DataCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        // A&D peripherals have a DATE TIME characteristic, so write that first
        peripheral.getCharacteristic(SERVICE_UUID, DATE_TIME_CHARACTERISTIC_UUID)?.let {
            writeDateTime(peripheral, it)
        }

        peripheral.setNotify(SERVICE_UUID, TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID, true)
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        when (characteristic.uuid) {
            TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID -> {
                callback?.onSimpleObservation(TemperatureMeasurement.fromBytes(value).asObservation(peripheral))
                startDisconnectTimer(peripheral)
            }
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb")
        val TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb")
        val DATE_TIME_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb")
    }
}