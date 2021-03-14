package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.ObservationsCallback
import com.welie.healthhub.observations.SystemInfoStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class DeviceInformationServiceHandler : ServiceHandler() {

    override val TAG: String = "DeviceInformationServiceHandler"
    override var callback: ObservationsCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        super.onCharacteristicsDiscovered(peripheral, characteristics)
        peripheral.readCharacteristic(SERVICE_UUID, MANUFACTURER_NAME_CHARACTERISTIC_UUID)
        peripheral.readCharacteristic(SERVICE_UUID, MODEL_NUMBER_CHARACTERISTIC_UUID)
        peripheral.readCharacteristic(SERVICE_UUID, SERIAL_NUMBER_CHARACTERISTIC_UUID)
        peripheral.readCharacteristic(SERVICE_UUID, FIRMWARE_REVISION_CHARACTERISTIC_UUID)
        peripheral.readCharacteristic(SERVICE_UUID, SOFTWARE_REVISION_CHARACTERISTIC_UUID)
        peripheral.readCharacteristic(SERVICE_UUID, HARDWARE_REVISION_CHARACTERISTIC_UUID)
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        try {
            val stringValue = BluetoothBytesParser(value).stringValue
            when(characteristic.uuid) {
                MANUFACTURER_NAME_CHARACTERISTIC_UUID -> SystemInfoStore.get(peripheral.address).manufacturer = stringValue
                MODEL_NUMBER_CHARACTERISTIC_UUID -> SystemInfoStore.get(peripheral.address).model = stringValue
                SERIAL_NUMBER_CHARACTERISTIC_UUID -> SystemInfoStore.get(peripheral.address).serialNumber = stringValue
                HARDWARE_REVISION_CHARACTERISTIC_UUID -> SystemInfoStore.get(peripheral.address).hardwareVersion = stringValue
                FIRMWARE_REVISION_CHARACTERISTIC_UUID -> SystemInfoStore.get(peripheral.address).firmwareVersion = stringValue
                SOFTWARE_REVISION_CHARACTERISTIC_UUID -> SystemInfoStore.get(peripheral.address).softwareVersion = stringValue
            }
        } catch (exception: Exception) {
            logger.error("could not parse <${BluetoothBytesParser.bytes2String(value)}> for <${characteristic.uuid}>")
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
        val MANUFACTURER_NAME_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb")
        val MODEL_NUMBER_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb")
        val SERIAL_NUMBER_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb")
        val HARDWARE_REVISION_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb")
        val FIRMWARE_REVISION_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb")
        val SOFTWARE_REVISION_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb")
    }
}