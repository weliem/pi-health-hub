package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.ObservationLocation

fun BluetoothPeripheral.turnOffAllNotifications() {
    // Turn off notifications for all characteristics that are notifying
    // We do this because Bluez remembers notification state between connections but peripherals don't
    this.notifyingCharacteristics.forEach { this.setNotify(it, false) }
}

fun BluetoothPeripheral.isANDPeripheral(): Boolean {
    val name = this.name ?: ""
    return name.contains("352BLE") || name.contains("651BLE") || name.contains("201BLE")
}

fun BluetoothPeripheral.reconnectionDelay(): Long {
    val name = this.name ?: ""
    return if (name.contains("TAIDOC")) 40000
    else 10000
}

fun BluetoothPeripheral.measurementLocation(): ObservationLocation {
    val name = this.name ?: ""
    if (name.contains("61BLE")) {
        return ObservationLocation.Arm
    } else if(name.contains("201BLE")) {
        return ObservationLocation.Armpit
    } else {
        return ObservationLocation.Unknown
    }
}