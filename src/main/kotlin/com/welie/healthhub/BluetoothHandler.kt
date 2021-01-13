package com.welie.healthhub

import com.welie.blessed.*
import com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8
import com.welie.blessed.BluetoothCentral.SCANOPTION_NO_NULL_NAMES
import java.util.*
import java.util.concurrent.ScheduledFuture
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JFrame
import javax.swing.JOptionPane
import java.util.UUID
import com.welie.blessed.BluetoothGattCharacteristic.WriteType

import com.welie.blessed.BluetoothPeripheral


class BluetoothHandler {
    private val logger: Logger = LoggerFactory.getLogger(TAG)
    private val handler: Handler = Handler(TAG)
    private var justBonded = false
    private var timeoutFuture: ScheduledFuture<*>? = null
    private lateinit var callback: DataCallback
    private lateinit var frame: JFrame

    private val peripheralCallback: BluetoothPeripheralCallback = object : BluetoothPeripheralCallback() {

        override fun onServicesDiscovered(peripheral: BluetoothPeripheral) {
            peripheral.readCharacteristic(DIS_SERVICE_UUID, MANUFACTURER_NAME_CHARACTERISTIC_UUID)
            peripheral.readCharacteristic(DIS_SERVICE_UUID, MODEL_NUMBER_CHARACTERISTIC_UUID)

            peripheral.getCharacteristic(CTS_SERVICE_UUID, CURRENT_TIME_CHARACTERISTIC_UUID)?.let {
                peripheral.setNotify(it, true)

                // If it has the write property, we write the current time
                if (it.supportsWritingWithResponse()) {
                    val parser = BluetoothBytesParser()
                    parser.setCurrentTime(Calendar.getInstance())
                    peripheral.writeCharacteristic(it, parser.value, WriteType.withResponse)
                }
            }

            peripheral.readCharacteristic(BTS_SERVICE_UUID, BATTERY_LEVEL_CHARACTERISTIC_UUID)
            peripheral.setNotify(BLP_SERVICE_UUID, BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID, true)
            peripheral.setNotify(HTS_SERVICE_UUID, TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID, true)
            peripheral.setNotify(PLX_SERVICE_UUID, PLX_CONTINUOUS_MEASUREMENT_CHAR_UUID, true)
            peripheral.setNotify(PLX_SERVICE_UUID, PLX_SPOT_MEASUREMENT_CHAR_UUID, true)
            peripheral.setNotify(HRS_SERVICE_UUID, HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID, true)
            peripheral.setNotify(WSS_SERVICE_UUID, WSS_MEASUREMENT_CHAR_UUID, true)
            peripheral.setNotify(GLS_SERVICE_UUID, GLS_MEASUREMENT_CHARACTERISTIC_UUID, true);
            peripheral.setNotify(GLS_SERVICE_UUID, GLS_MEASUREMENT_CONTEXT_CHARACTERISTIC_UUID, true);
            peripheral.setNotify(GLS_SERVICE_UUID, GLS_RECORD_ACCESS_POINT_CHARACTERISTIC_UUID, true);
        }

        override fun onNotificationStateUpdate(
            peripheral: BluetoothPeripheral,
            characteristic: BluetoothGattCharacteristic,
            status: BluetoothCommandStatus
        ) {
            if (status === BluetoothCommandStatus.COMMAND_SUCCESS) {
                val isNotifying: Boolean = peripheral.isNotifying(characteristic)
                logger.info("SUCCESS: Notify set to '$isNotifying' for ${characteristic.uuid}")
                if (isNotifying) {
                    // If we just bonded wit the A&D 651BLE, issue a disconnect to finish the pairing process
                    val peripheralName = peripheral.name ?: ""
                    if (justBonded && peripheralName.contains("651BLE")) {
                        peripheral.cancelConnection()
                        justBonded = false
                    }

                    if (characteristic.uuid == GLS_RECORD_ACCESS_POINT_CHARACTERISTIC_UUID) {
                        writeGetAllGlucoseMeasurements(peripheral)
                    }
                }
            } else {
                logger.error("ERROR: Changing notification state failed for <${characteristic.uuid}>")
            }
        }

        override fun onCharacteristicUpdate(
            peripheral: BluetoothPeripheral,
            value: ByteArray,
            characteristic: BluetoothGattCharacteristic,
            status: BluetoothCommandStatus
        ) {
            if (status != BluetoothCommandStatus.COMMAND_SUCCESS) {
                logger.error("command failed with status $status")
                return
            }

            val parser = BluetoothBytesParser(value)
            when (characteristic.uuid) {
                MANUFACTURER_NAME_CHARACTERISTIC_UUID -> {
                    val manufacturer: String = parser.getStringValue(0)
                    logger.info("Received manufacturer: '$manufacturer'")
                }
                MODEL_NUMBER_CHARACTERISTIC_UUID -> {
                    val modelNumber: String = parser.getStringValue(0)
                    logger.info("Received modelnumber: '$modelNumber'")
                }
                TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID -> {
                    val measurement = TemperatureMeasurement(value)
                    logger.info(measurement.toString())
                    callback.onTemperature(measurement)
                    startDisconnectTimer(peripheral)
                }
                BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID -> {
                    val measurement = BloodPressureMeasurement(value)
                    logger.info(measurement.toString())
                    callback.onBloodPressure(measurement)
                    startDisconnectTimer(peripheral)
                }
                PLX_CONTINUOUS_MEASUREMENT_CHAR_UUID -> {
                    val measurement = PulseOximeterContinuousMeasurement(value)
                    logger.info(measurement.toString())
                }
                PLX_SPOT_MEASUREMENT_CHAR_UUID -> {
                    val measurement = PulseOximeterSpotMeasurement(value)
                    logger.info(measurement.toString())
                    callback.onBloodOxygen(measurement)
                    startDisconnectTimer(peripheral)
                }
                HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID -> {
                    val measurement = HeartRateMeasurement(value)
                    logger.info(measurement.toString())
                    callback.onHeartRate(measurement)
                }
                WSS_MEASUREMENT_CHAR_UUID -> {
                    val measurement = WeightMeasurement(value)
                    logger.info(measurement.toString())
                    callback.onWeight(measurement)
                }
                CURRENT_TIME_CHARACTERISTIC_UUID -> {
                    val currentTime: Date = parser.dateTime
                    logger.info("Received device time: $currentTime")
                }
                BATTERY_LEVEL_CHARACTERISTIC_UUID -> {
                    val batteryLevel: Int = parser.getIntValue(FORMAT_UINT8)
                    logger.info("battery level $batteryLevel")
                }
            }
        }

        override fun onBondingStarted(peripheral: BluetoothPeripheral) {
            logger.info("bonding started")
        }

        override fun onBondingSucceeded(peripheral: BluetoothPeripheral) {
            logger.info("bonding succeeded")
            justBonded = true
        }

        override fun onBondingFailed(peripheral: BluetoothPeripheral) {
            logger.info("bonding failed")
        }

        private fun writeGetAllGlucoseMeasurements(peripheral: BluetoothPeripheral) {
            val OP_CODE_REPORT_STORED_RECORDS: Byte = 1
            val OPERATOR_ALL_RECORDS: Byte = 1
            val command = byteArrayOf(OP_CODE_REPORT_STORED_RECORDS, OPERATOR_ALL_RECORDS)
            peripheral.writeCharacteristic(
                GLS_SERVICE_UUID,
                GLS_RECORD_ACCESS_POINT_CHARACTERISTIC_UUID,
                command,
                WriteType.withResponse
            )
        }
    }

    fun startDisconnectTimer(peripheral: BluetoothPeripheral) {
        // Cancel timer if it was already set
        timeoutFuture?.cancel(false)
        timeoutFuture = null

        // Start a new timer
        timeoutFuture = handler.postDelayed({ peripheral.cancelConnection() }, 2000L)
    }

    private val bluetoothCentralCallback: BluetoothCentralCallback = object : BluetoothCentralCallback() {
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
            }, 40000L)
        }

        override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
            // See if this device is on the blacklist
            val peripheralAddress: String = peripheral.address
            val blacklisted = blackList.contains(peripheralAddress)
            if (blacklisted) return

            // Not blacklisted so put it on the blacklist and connect to it
            blackList.add(peripheralAddress)
            logger.info(scanResult.toString())
            handler.postDelayed( {central.connectPeripheral(peripheral, peripheralCallback)}, 1000)
        }

        override fun onPinRequest(peripheral: BluetoothPeripheral): String {
            val pin = JOptionPane.showInputDialog(frame, "Enter PIN code for this peripheral", "Enter PIN", JOptionPane.PLAIN_MESSAGE, null, null, "" ) as String
            return pin
        }
    }

    fun startScanning() {
        central.scanForPeripheralsWithServices(
            arrayOf(
                WSS_SERVICE_UUID,
                HTS_SERVICE_UUID,
                PLX_SERVICE_UUID,
                BLP_SERVICE_UUID,
                HRS_SERVICE_UUID,
                GLS_SERVICE_UUID
            )
        )
    }

    fun setDataCallback(callback: DataCallback) {
        this.callback = callback
    }

    fun setFrame(frame: JFrame) {
        this.frame = frame
    }

    companion object {
        private const val TAG = "com.welie.healthhub.BluetoothHandler"
        private val blackList: MutableList<String> = ArrayList()

        // UUIDs for the Blood Pressure service (BLP)
        private val BLP_SERVICE_UUID: UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb")
        private val BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A35-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Health Thermometer service (HTS)
        private val HTS_SERVICE_UUID: UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb")
        private val TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Heart Rate service (HRS)
        private val HRS_SERVICE_UUID: UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
        private val HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Device Information service (DIS)
        private val DIS_SERVICE_UUID: UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
        private val MANUFACTURER_NAME_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb")
        private val MODEL_NUMBER_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Current Time service (CTS)
        private val CTS_SERVICE_UUID: UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")
        private val CURRENT_TIME_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A2B-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Battery Service (BAS)
        private val BTS_SERVICE_UUID: UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")
        private val BATTERY_LEVEL_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Pulse Oximeter Service (PLX)
        val PLX_SERVICE_UUID: UUID = UUID.fromString("00001822-0000-1000-8000-00805f9b34fb")
        private val PLX_SPOT_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002a5e-0000-1000-8000-00805f9b34fb")
        private val PLX_CONTINUOUS_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002a5f-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Weight Scale Service (WSS)
        val WSS_SERVICE_UUID: UUID = UUID.fromString("0000181D-0000-1000-8000-00805f9b34fb")
        private val WSS_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002A9D-0000-1000-8000-00805f9b34fb")

        val GLS_SERVICE_UUID: UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb")
        val GLS_MEASUREMENT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb")
        val GLS_RECORD_ACCESS_POINT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A52-0000-1000-8000-00805f9b34fb")
        val GLS_MEASUREMENT_CONTEXT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A34-0000-1000-8000-00805f9b34fb")
    }

    private val central: BluetoothCentral = BluetoothCentral(bluetoothCentralCallback, setOf(SCANOPTION_NO_NULL_NAMES))

    init {
        logger.info("initializing BluetoothCentral")
        startScanning()
    }
}