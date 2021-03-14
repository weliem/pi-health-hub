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
import com.welie.healthhub.observations.ObservationsCallback
import com.welie.healthhub.observations.SystemInfo
import com.welie.healthhub.observations.SystemInfoStore
import kotlin.collections.HashMap


class BluetoothHandler {
    private val logger: Logger = LoggerFactory.getLogger(TAG)
    private val handler: Handler = Handler(TAG)
    private lateinit var frame: JFrame
    private val serviceHandlers: MutableMap<UUID, ServiceHandler> = HashMap()

    private val peripheralCallback: BluetoothPeripheralCallback = object : BluetoothPeripheralCallback() {

        override fun onServicesDiscovered(peripheral: BluetoothPeripheral, services: MutableList<BluetoothGattService>) {
            val orderedServices = orderServices(services)
            orderedServices.forEach { serviceHandlers[it.uuid]?.onCharacteristicsDiscovered(peripheral,it.characteristics) }
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

        private fun orderServices(services :List<BluetoothGattService>): List<BluetoothGattService> {
            val preferredOrder = arrayOf(DeviceInformationServiceHandler.SERVICE_UUID, CurrentTimeServiceHandler.SERVICE_UUID, BatteryServiceHandler.SERVICE_UUID)
            val orderedServices: MutableList<BluetoothGattService> = ArrayList()

            for (uuid in preferredOrder) {
                for (service in services) {
                    if (service.uuid == uuid) {
                        orderedServices.add(service)
                    }
                }
            }

            for (service in services) {
                if (!orderedServices.contains(service)) {
                    orderedServices.add(service)
                }
            }
            return orderedServices
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
                startScanning()
            }, peripheral.reconnectionDelay())
        }

        override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
            // See if this device is on the blacklist
            val peripheralAddress: String = peripheral.address
            val blacklisted = blackList.contains(peripheralAddress)
            if (blacklisted) return

            // Not blacklisted so put it on the blacklist and connect to it
            blackList.add(peripheralAddress)
            SystemInfoStore.add(SystemInfo(peripheral.address))
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

    fun setDataCallback(callback: ObservationsCallback) {
        serviceHandlers.forEach { it.value.callback = callback }
    }

    fun setFrame(frame: JFrame) {
        this.frame = frame
    }

    companion object {
        private const val TAG = "com.welie.healthhub.BluetoothHandler"
        private val blackList: MutableList<String> = ArrayList()
    }

    private val central: BluetoothCentralManager = BluetoothCentralManager(bluetoothCentralCallback, setOf(SCANOPTION_NO_NULL_NAMES))

    init {
        logger.info("initializing BluetoothCentral")
        serviceHandlers[DeviceInformationServiceHandler.SERVICE_UUID] = DeviceInformationServiceHandler()
        serviceHandlers[CurrentTimeServiceHandler.SERVICE_UUID] = CurrentTimeServiceHandler()
        serviceHandlers[BatteryServiceHandler.SERVICE_UUID] = BatteryServiceHandler()
        serviceHandlers[BloodPressureServiceHandler.SERVICE_UUID] = BloodPressureServiceHandler()
        serviceHandlers[TemperatureServiceHandler.SERVICE_UUID] = TemperatureServiceHandler()
        serviceHandlers[WeightServiceHandler.SERVICE_UUID] = WeightServiceHandler()
        serviceHandlers[HeartRateServiceHandler.SERVICE_UUID] = HeartRateServiceHandler()
        serviceHandlers[GlucoseServiceHandler.SERVICE_UUID] = GlucoseServiceHandler()
        serviceHandlers[PulseOximeterServiceHandler.SERVICE_UUID] = PulseOximeterServiceHandler()
        startScanning()
    }
}