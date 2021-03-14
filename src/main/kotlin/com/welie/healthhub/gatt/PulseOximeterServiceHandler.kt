package com.welie.healthhub.gatt

import com.welie.blessed.BluetoothCommandStatus
import com.welie.blessed.BluetoothGattCharacteristic
import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.ObservationsCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class PulseOximeterServiceHandler : ServiceHandler() {

    override val TAG: String = "PulseOximeterServiceHandler"
    override var callback: ObservationsCallback? = null
    override val logger: Logger = LoggerFactory.getLogger(TAG)

    override fun onCharacteristicsDiscovered(peripheral: BluetoothPeripheral, characteristics: List<BluetoothGattCharacteristic>) {
        peripheral.setNotify(SERVICE_UUID, CONTINUOUS_MEASUREMENT_CHAR_UUID, true)
        peripheral.setNotify(SERVICE_UUID, SPOT_MEASUREMENT_CHAR_UUID, true)
    }

    override fun onCharacteristicChanged(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: BluetoothCommandStatus) {
        super.onCharacteristicChanged(peripheral, value, characteristic, status)

        when(characteristic.uuid) {
            SPOT_MEASUREMENT_CHAR_UUID -> {
                callback?.onObservationList(PulseOximeterSpotMeasurement.fromBytes(value).asObservationList(peripheral))
                startDisconnectTimer(peripheral)
            }
            CONTINUOUS_MEASUREMENT_CHAR_UUID -> {
                // later
            }
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001822-0000-1000-8000-00805f9b34fb")
        val SPOT_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002a5e-0000-1000-8000-00805f9b34fb")
        val CONTINUOUS_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002a5f-0000-1000-8000-00805f9b34fb")
    }
}