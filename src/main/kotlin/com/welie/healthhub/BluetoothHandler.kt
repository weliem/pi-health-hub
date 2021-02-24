@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection"
)

package com.welie.healthhub

import com.welie.blessed.*
import com.welie.blessed.BluetoothBytesParser.*
import com.welie.blessed.BluetoothCentralManager.SCANOPTION_NO_NULL_NAMES
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JFrame
import javax.swing.JOptionPane
import java.util.UUID

import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.gatt.*
import kotlin.collections.HashMap


class BluetoothHandler {
    private val logger: Logger = LoggerFactory.getLogger(TAG)
    private val handler: Handler = Handler(TAG)
    private lateinit var frame: JFrame
    private val serviceHandlers: MutableMap<UUID, ServiceHandler> = HashMap()

    private val peripheralCallback: BluetoothPeripheralCallback = object : BluetoothPeripheralCallback() {

        override fun onServicesDiscovered(peripheral: BluetoothPeripheral) {
            peripheral.services.forEach { serviceHandlers[it.uuid]?.onCharacteristicsDiscovered(peripheral,it.characteristics) }
        }

        override fun onNotificationStateUpdate(peripheral: BluetoothPeripheral, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
            serviceHandlers[characteristic.service?.uuid]?.onNotificationStateUpdated(peripheral, characteristic, status)
        }

        override fun onCharacteristicWrite(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
            serviceHandlers[characteristic.service?.uuid]?.onCharacteristicWrite(peripheral, value, characteristic, status)

        }
        override fun onCharacteristicUpdate(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
            try {
                serviceHandlers[characteristic.service?.uuid]?.onCharacteristicChanged(peripheral, value, characteristic, status)
            } catch (ex : Exception) {
                logger.error("error parsing ${bytes2String(value)}")
            }
        }
    }


    private val bluetoothCentralCallback: BluetoothCentralManagerCallback = object : BluetoothCentralManagerCallback() {
        override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
            logger.info("connected peripheral")
        }

        override fun onConnectionFailed(peripheral: BluetoothPeripheral, status: BluetoothCommandStatus) {
            logger.info("connection failed with status $status")
            handler.postDelayed({ blackList.remove(peripheral.address) }, 2000L)
        }

        override fun onDisconnectedPeripheral(peripheral: BluetoothPeripheral, status: BluetoothCommandStatus) {
            logger.info("disconnected peripheral")
            val peripheralAddress: String = peripheral.address
            handler.postDelayed({
                logger.info("removing '$peripheralAddress' from blacklist")
                blackList.remove(peripheralAddress)
            }, peripheral.reconnectionDelay())
        }

        override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
            // See if this device is on the blacklist
            val peripheralAddress: String = peripheral.address
            val blacklisted = blackList.contains(peripheralAddress)
            if (blacklisted) return

            // Not blacklisted so put it on the blacklist and connect to it
            blackList.add(peripheralAddress)
            logger.info(scanResult.toString())
            handler.postDelayed({ central.connectPeripheral(peripheral, peripheralCallback) }, 1000)
        }

        override fun onPinRequest(peripheral: BluetoothPeripheral): String {
            return JOptionPane.showInputDialog(
                frame,
                "Enter PIN code for this peripheral",
                "Enter PIN",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                ""
            ) as String
        }
    }

    private fun startScanning() {
        central.scanForPeripheralsWithServices(serviceHandlers.keys.toTypedArray())
    }

    fun setDataCallback(callback: DataCallback) {
        serviceHandlers.forEach { it.value.callback = callback }
    }

    fun setFrame(frame: JFrame) {
        this.frame = frame
    }

    companion object {
        private const val TAG = "com.welie.healthhub.BluetoothHandler"
        private val blackList: MutableList<String> = ArrayList()

        // UUIDs for the Device Information service (DIS)
        private val DIS_SERVICE_UUID: UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
        private val MANUFACTURER_NAME_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb")
        private val MODEL_NUMBER_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Current Time service (CTS)
        private val CTS_SERVICE_UUID: UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")
        private val CURRENT_TIME_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A2B-0000-1000-8000-00805f9b34fb")
        private val DATE_TIME_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Battery Service (BAS)
        private val BTS_SERVICE_UUID: UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")
        private val BATTERY_LEVEL_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb")

        // Thingy service
        val THINGY_SERVICE: UUID = UUID.fromString("EF680100-9B35-4933-9B10-52FFA9740042")
        val THINGY_ENVIRONMENTAL_SERVICE: UUID = UUID.fromString("EF680200-9B35-4933-9B10-52FFA9740042")
        val THINGY_TEMPERATURE: UUID = UUID.fromString("EF680201-9B35-4933-9B10-52FFA9740042")
        val THINGY_PRESSURE: UUID = UUID.fromString("EF680202-9B35-4933-9B10-52FFA9740042")
    }

    private val central: BluetoothCentralManager =
        BluetoothCentralManager(bluetoothCentralCallback, setOf(SCANOPTION_NO_NULL_NAMES))

    init {
        logger.info("initializing BluetoothCentral")
        serviceHandlers[BloodPressureServiceHandler.SERVICE_UUID] = BloodPressureServiceHandler()
        serviceHandlers[TemperatureServiceHandler.SERVICE_UUID] = TemperatureServiceHandler()
        serviceHandlers[WeightServiceHandler.SERVICE_UUID] = WeightServiceHandler()
        serviceHandlers[HeartRateServiceHandler.SERVICE_UUID] = HeartRateServiceHandler()
        serviceHandlers[GlucoseServiceHandler.SERVICE_UUID] = GlucoseServiceHandler()
        serviceHandlers[PulseOximeterServiceHandler.SERVICE_UUID] = PulseOximeterServiceHandler()
        startScanning()
    }
}