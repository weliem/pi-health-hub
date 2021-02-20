package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral

interface DataCallback {
    fun onTemperature(measurement : TemperatureMeasurement, peripheral: BluetoothPeripheral)
    fun onBloodPressure(measurement: BloodPressureMeasurement, peripheral: BluetoothPeripheral)
    fun onWeight(measurement: WeightMeasurement, peripheral: BluetoothPeripheral)
    fun onHeartRate(measurement: HeartRateMeasurement, peripheral: BluetoothPeripheral)
    fun onBloodOxygen(measurement: PulseOximeterSpotMeasurement, peripheral: BluetoothPeripheral)
    fun onBloodGlucose(measurement: GlucoseMeasurement, peripheral: BluetoothPeripheral)
    fun onAirPressure(pressure: Float, peripheral: BluetoothPeripheral)
}