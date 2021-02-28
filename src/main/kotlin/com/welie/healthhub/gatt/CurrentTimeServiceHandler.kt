package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.DataCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CurrentTimeServiceHandler : ServiceHandler() {
    override val TAG: String = "CurrentTimeServiceHandler"
    override var callback: DataCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        super.onCharacteristicsDiscovered(peripheral, characteristics)
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")
        val DATE_TIME_CHARACTERISTIC_UUID = UUID.fromString("00002A08-0000-1000-8000-00805f9b34fb")
        val CURRENT_TIME_CHARACTERISTIC_UUID = UUID.fromString("00002A2B-0000-1000-8000-00805f9b34fb")
        val LOCAL_TIME_INFORMATION_CHARACTERISTIC_UUID = UUID.fromString("00002A0F-0000-1000-8000-00805f9b34fb")
        val REFERENCE_TIME_INFORMATION_CHARACTERISTIC_UUID = UUID.fromString("00002A14-0000-1000-8000-00805f9b34fb")
    }
}