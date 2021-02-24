package com.welie.healthhub.gatt

import com.welie.blessed.*
import com.welie.healthhub.DataCallback
import com.welie.healthhub.isANDPeripheral
import com.welie.healthhub.turnOffAllNotifications
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ScheduledFuture


abstract class ServiceHandler {

    abstract val TAG: String
   // abstract val serviceUUID: UUID
    abstract var callback: DataCallback?

    private var timeoutFuture: ScheduledFuture<*>? = null
    val logger: Logger = LoggerFactory.getLogger(TAG)
    private val handler: Handler = Handler(TAG)

    /**
     * Called when notification is successfully subscribed for a characteristic.
     * @param peripheral device instance
     * @param characteristics list of Characteristics discovered
     */
    open fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        logger.info(TAG, "Discovered characteristics (${characteristics.size}) for ${peripheral.name}")
    }

    /**
     * Called when notification is successfully subscribed for a characteristic.
     * @param peripheral device instance
     * @param characteristic Characteristic for which notify is enabled
     */
    open fun onNotificationStateUpdated(peripheral: BluetoothPeripheral, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        if (status === BluetoothCommandStatus.COMMAND_SUCCESS) {
            if (peripheral.isNotifying(characteristic)) {
                if (peripheral.isANDPeripheral()) {
                    startDisconnectTimer(peripheral)
                }
            } else {
                // Apparently we are turning off notifications as part of a controlled disconnect
                if (peripheral.notifyingCharacteristics.isEmpty()) {
                    peripheral.cancelConnection()
                }
            }
        } else {
            logger.error("changing notification state failed for <${characteristic.uuid}>")
        }
    }

    /**
     * Process characteristic value update in service handler.
     * @param peripheral  Device address which received characteristic value update.
     * @param value update data byte array
     * @param characteristic Characteristic that received value update.
     */
    open fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        if (status != BluetoothCommandStatus.COMMAND_SUCCESS) {
            logger.error("command failed with status $status")
            return
        }
    }

    /**
     * Called when a write command has been completed.
     * @param peripheral Device object
     * @param characteristic The characteristic that was written on
     * @param status status code of the write operation
     */
    open fun onCharacteristicWrite(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        logger.info(TAG, "${if (status == BluetoothCommandStatus.COMMAND_SUCCESS) "SUCCESS" else "ERROR"}: Write to ${characteristic.uuid}")
    }

    fun startDisconnectTimer(peripheral: BluetoothPeripheral) {
        // Cancel timer if it was already set
        timeoutFuture?.cancel(false)
        timeoutFuture = handler.postDelayed({ peripheral.turnOffAllNotifications() }, 2000L)
    }

    fun writeDateTime(peripheral: BluetoothPeripheral, characteristic: BluetoothGattCharacteristic) {
        val parser = BluetoothBytesParser()
        parser.setDateTime(Calendar.getInstance())
        peripheral.writeCharacteristic(characteristic, parser.value, BluetoothGattCharacteristic.WriteType.WITH_RESPONSE)
    }
}