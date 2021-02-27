package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.DataCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class HeartRateServiceHandler : ServiceHandler() {

    override val TAG: String = "HeartRateServiceHandler"
    override var callback: DataCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        peripheral.setNotify(SERVICE_UUID, HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID, true)
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        when(characteristic.uuid) {
            HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID -> {
                callback?.onHeartRate(HeartRateMeasurement.fromBytes(value), peripheral)
            }
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
        val HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")
    }
}