package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.DataCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class BloodPressureServiceHandler() : ServiceHandler() {

    override val TAG: String = "BloodPressureServiceHandler"
    override var callback: DataCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        // A&D peripherals have a DATE TIME characteristic, so write that first
        peripheral.getCharacteristic(SERVICE_UUID, DATE_TIME_CHARACTERISTIC_UUID)?.let {
            writeDateTime(peripheral, it)
            peripheral.readCharacteristic(it)
        }

        peripheral.readCharacteristic(SERVICE_UUID, BLOOD_PRESSURE_FEATURE_CHARACTERISTIC_UUID)
        peripheral.setNotify(SERVICE_UUID, BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID, true)
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        when (characteristic.uuid) {
            BLOOD_PRESSURE_FEATURE_CHARACTERISTIC_UUID -> {
                //callback?.onBloodPressureFeature(BloodPressureFeature.fromBytes(value), peripheral)
            }
            BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID -> {
                callback?.onObservationList(BloodPressureMeasurement.fromBytes(value).asObservationList(peripheral))
                startDisconnectTimer(peripheral)
            }
            INTERMEDIATE_CUFF_PRESSURE_CHARACTERISTIC_UUID -> {
                //callback?.onIntermediateCuffPressure(BloodPressureMeasurement.fromBytes(value), peripheral)
            }
            DATE_TIME_CHARACTERISTIC_UUID -> {
                callback?.onPeripheralTime(BluetoothBytesParser(value).dateTime, peripheral.address)
            }
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb")
        val BLOOD_PRESSURE_FEATURE_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a49-0000-1000-8000-00805f9b34fb")
        val BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A35-0000-1000-8000-00805f9b34fb")
        val INTERMEDIATE_CUFF_PRESSURE_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A36-0000-1000-8000-00805f9b34fb")
        val DATE_TIME_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb")
    }
}