package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothCommandStatus.COMMAND_SUCCESS
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothGattCharacteristic.WriteType.WITH_RESPONSE
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.DataCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class GlucoseServiceHandler: ServiceHandler() {
    override val TAG: String = "GlucoseServiceHandler"
    override var callback: DataCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        peripheral.setNotify(SERVICE_UUID, MEASUREMENT_CHARACTERISTIC_UUID, true)
        peripheral.setNotify(SERVICE_UUID, RECORD_ACCESS_POINT_CHARACTERISTIC_UUID, true)
    }

    override fun onNotificationStateUpdated(peripheral: BluetoothPeripheral, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onNotificationStateUpdated(peripheral, characteristic, status)

        if (status == COMMAND_SUCCESS && peripheral.isNotifying(characteristic)) {
            if (characteristic.uuid == RECORD_ACCESS_POINT_CHARACTERISTIC_UUID) {
                writeGetAllGlucoseMeasurements(peripheral)
            }
        }
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        when(characteristic.uuid) {
            MEASUREMENT_CHARACTERISTIC_UUID -> {
                callback?.onBloodGlucose(GlucoseMeasurement.fromBytes(value), peripheral)
                startDisconnectTimer(peripheral)
            }
        }
    }

    private fun writeGetAllGlucoseMeasurements(peripheral: BluetoothPeripheral) {
        val opCodeReportStoredRecords: Byte = 1
        val operatorAllRecords: Byte = 1
        val command = byteArrayOf(opCodeReportStoredRecords, operatorAllRecords)
        peripheral.writeCharacteristic(SERVICE_UUID, RECORD_ACCESS_POINT_CHARACTERISTIC_UUID, command, WITH_RESPONSE)
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb")
        val MEASUREMENT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb")
        val RECORD_ACCESS_POINT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A52-0000-1000-8000-00805f9b34fb")
        val MEASUREMENT_CONTEXT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A34-0000-1000-8000-00805f9b34fb")
    }
}