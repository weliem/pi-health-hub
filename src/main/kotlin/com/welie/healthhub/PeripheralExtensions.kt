package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.ObservationLocation
import com.welie.healthhub.observations.SensorType

fun BluetoothPeripheral.turnOffAllNotifications() {
    // Turn off notifications for all characteristics that are notifying
    // We do this because Bluez remembers notification state between connections but peripherals don't
    this.notifyingCharacteristics.forEach { this.setNotify(it, false) }
}

fun BluetoothPeripheral.isANDPeripheral(): Boolean {
    val name = this.name ?: ""
    return name.contains("352BLE") || name.contains("651BLE") || name.contains("201BLE")
}

fun BluetoothPeripheral.isPhilipsThermometer(): Boolean {
    val name = this.name ?: ""
    return name.startsWith("Philips ear thermometer") || name.startsWith("SCH740")
}

fun BluetoothPeripheral.reconnectionDelay(): Long {
    val name = this.name ?: ""
    return if (name.contains("TAIDOC TD1241")) 40000
    else if(name.contains("51-102")) 23000
    else 10000
}

fun BluetoothPeripheral.measurementLocation(): ObservationLocation {
    val name = this.name ?: ""
    if (name.contains("651BLE")) {
        return ObservationLocation.Arm
    } else if(name.contains("201BLE")) {
        return ObservationLocation.Armpit
    } else if(name.startsWith("H7") || name.startsWith("H10")) {
        return ObservationLocation.Chest
    } else {
        return ObservationLocation.Unknown
    }
}

fun BluetoothPeripheral.sensorType(): SensorType {
    val name = this.name ?: ""

    if (name.startsWith("H7") || name.startsWith("H9") || name.startsWith("H10")) {
        return SensorType.EcgSensor
    }

    if (name.startsWith("OH1") || name.startsWith("Nonin")) {
        return SensorType.PggSensor
    }

    if (isPhilipsThermometer() || name.startsWith("TAIDOC TD1241")) {
        return SensorType.InfraRedSensor
    }

    if (name.startsWith("352BLE") || name.startsWith("51-102")) {
        return SensorType.LoadCell
    }

    return SensorType.Unknown
}
