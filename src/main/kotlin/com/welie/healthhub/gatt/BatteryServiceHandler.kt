package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothBytesParser
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.DataCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class BatteryServiceHandler: ServiceHandler() {
    override val TAG: String = "CurrentTimeServiceHandler"
    override var callback: DataCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        peripheral.readCharacteristic(SERVICE_UUID, BATTERY_LEVEL_CHARACTERISTIC_UUID)
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        try {
            val parser = BluetoothBytesParser(value)
            when(characteristic.uuid) {
                BATTERY_LEVEL_CHARACTERISTIC_UUID -> callback?.onBatteryPercentage(parser.getIntValue(FORMAT_UINT8), peripheral.address)
            }
        } catch (exception: Exception) {
            logger.error("could not parse <${BluetoothBytesParser.bytes2String(value)}> for <${characteristic.uuid}>")
        }
    }
    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")
        val BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb")
    }
}