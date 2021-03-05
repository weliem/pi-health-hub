package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.observations.ObservationLocation
import com.welie.healthhub.observations.SensorType

const val philipsEarThermometer = "Philips ear thermometer" // Fixed
const val aventEarThermometer = "SCH740" // Fixed
const val andScale = "A&D_UC-352BLE_" // Starts with
const val andBpm = "A&D_UC-651BLE_" // Starts with
const val andThermometer = "A&D_UC-201BLE_" // Starts with
const val indieHealthScale = "51-102" // Fixed
const val taidocThermometer1241 = "TAIDOC TD1241" // Fixed
const val polarH7 = "Polar H7" // Starts with
const val polarH10 = "Polar H10" // Starts with
const val polarOH1 = "Polar OH1" // Starts with
const val nonin3230 = "Nonin 3230" // Starts with

fun BluetoothPeripheral.turnOffAllNotifications() {
    // Turn off notifications for all characteristics that are notifying
    // We do this because Bluez remembers notification state between connections but peripherals don't
    this.notifyingCharacteristics.forEach { this.setNotify(it, false) }
}

fun BluetoothPeripheral.isANDPeripheral(): Boolean {
    return isAndScale() || isAndBpm() || isAndThermometer()
}

fun BluetoothPeripheral.isTaidocThermometer1241(): Boolean {
    return name == taidocThermometer1241
}

fun BluetoothPeripheral.isIndieHealthScale(): Boolean {
    return name == indieHealthScale
}

fun BluetoothPeripheral.isAndScale(): Boolean {
    return name.startsWith(andScale)
}

fun BluetoothPeripheral.isAndBpm(): Boolean {
    return name.startsWith(andBpm)
}

fun BluetoothPeripheral.isAndThermometer(): Boolean {
    return name.startsWith(andThermometer)
}

fun BluetoothPeripheral.isPhilipsThermometer(): Boolean {
    return name == philipsEarThermometer || name == (aventEarThermometer)
}

fun BluetoothPeripheral.isPolarH7(): Boolean {
    return name.startsWith(polarH7)
}

fun BluetoothPeripheral.isPolarH10(): Boolean {
    return name.startsWith(polarH10)
}

fun BluetoothPeripheral.isPolarOH1(): Boolean {
    return name.startsWith(polarOH1)
}

fun BluetoothPeripheral.isNonin3230(): Boolean {
    return name.startsWith(nonin3230)
}

fun BluetoothPeripheral.reconnectionDelay(): Long {
    return when {
        isTaidocThermometer1241() -> 40000
        isIndieHealthScale() -> 23000
        isAndScale() -> 15000
        else -> 10000
    }
}

fun BluetoothPeripheral.measurementLocation(): ObservationLocation {
    return when {
        isAndBpm() -> ObservationLocation.Arm
        isAndThermometer() -> ObservationLocation.Armpit
        isPolarH7() || isPolarH10() -> ObservationLocation.Chest
        else -> ObservationLocation.Unknown
    }
}

fun BluetoothPeripheral.sensorType(): SensorType {
    return when {
        isPolarH7() || isPolarH10() -> SensorType.EcgSensor
        isPolarOH1() || isNonin3230() -> SensorType.PggSensor
        isPhilipsThermometer() || isTaidocThermometer1241() -> SensorType.InfraRedSensor
        isAndScale() || isIndieHealthScale() -> SensorType.LoadCell
        else -> SensorType.Unknown
    }
}
